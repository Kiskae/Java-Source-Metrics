package nl.rug.jbi.jsm.core.execution;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import nl.rug.jbi.jsm.core.calculator.*;
import nl.rug.jbi.jsm.core.event.EventBus;
import nl.rug.jbi.jsm.core.pipeline.HandlerMap;
import nl.rug.jbi.jsm.core.pipeline.PipelineFrame;
import nl.rug.jbi.jsm.metrics.ClassSourceProducer;
import nl.rug.jbi.jsm.util.Pair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

class ControllerThread extends Thread {
    private final static Logger logger = LogManager.getLogger(ControllerThread.class);
    private final static AtomicInteger UNIQUE_EXECUTION_ID = new AtomicInteger(0);
    private final static List<MetricScope> SCOPE_EXECUTION_ORDER =
            ImmutableList.of(MetricScope.CLASS, MetricScope.PACKAGE, MetricScope.COLLECTION);
    private final static Function<Object, Class> OBJECT_GETCLASS = new Function<Object, Class>() {
        @Override
        public Class apply(Object metric) {
            return metric.getClass();
        }
    };
    private final PipelineExecutor executionPlan;
    private final ExecutorService executorPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private final AtomicInteger executionId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("JSM Execution Thread #%d", executionId.getAndIncrement()));
                }
            }
    );
    private final Map<String, EventBus> stateContainers = Maps.newHashMap();
    private final Table<MetricScope, String, List<Object>> dataForFutureScope = Tables.newCustomTable(
            Maps.<MetricScope, Map<String, List<Object>>>newEnumMap(MetricScope.class),
            new Supplier<Map<String, List<Object>>>() {
                @Override
                public Map<String, List<Object>> get() {
                    return Maps.newHashMap();
                }
            }
    );
    private final Predicate<List<MetricResult>> resultsCallback = new Predicate<List<MetricResult>>() {
        @Override
        public boolean apply(List<MetricResult> metricResults) {
            executionPlan.processResult(metricResults);
            return true;
        }
    };

    public ControllerThread(final PipelineExecutor executionPlan, final Set<String> classNames) {
        super(String.format("JSM Controller Thread #%d", ControllerThread.UNIQUE_EXECUTION_ID.getAndIncrement()));
        this.executionPlan = executionPlan;

        final HandlerMap classHandlerMap = this.executionPlan.getHandlerMap(MetricScope.CLASS);
        prepareEventBuses(classNames, classHandlerMap, this.stateContainers);
    }

    private static void prepareEventBuses(
            final Set<String> identifiers,
            final HandlerMap handlerMap,
            final Map<String, EventBus> containerMap
    ) {
        containerMap.clear();

        for (final String identifier : identifiers) {
            final EventBus eBus = new EventBus(identifier, handlerMap);
            containerMap.put(identifier, eBus);
        }
    }

    private static CountDownLatch createCountdownLatch(final int childTaskNum) {
        return new CountDownLatch(childTaskNum);
    }

    @Override
    public void run() {
        ClassSourceProducer.setCBCL(this.executionPlan.getDataSource());
        final Iterator<MetricScope> scopeIterator = SCOPE_EXECUTION_ORDER.iterator();

        MetricScope currentScope = scopeIterator.next();
        PipelineFrame currentFrame = executionPlan.getPipelineFrame(currentScope);

        final Queue<Pair<EventBus, Runnable>> taskQueue = Queues.newLinkedBlockingQueue();
        final Repository repo = this.executionPlan.getRepository();
        final ClassVisitorFactory cvFactory = this.executionPlan.getClassVisitorFactory();

        for (final Map.Entry<String, EventBus> entry : this.stateContainers.entrySet()) {
            try {
                final JavaClass jc = repo.loadClass(entry.getKey());
                taskQueue.add(new Pair<EventBus, Runnable>(
                        entry.getValue(),
                        cvFactory.createClassVisitor(jc, entry.getValue())
                ));
            } catch (ClassNotFoundException e) {
                //TODO: proper handling
                logger.debug(e);
            }
        }

        while (currentFrame != null) {
            logger.debug("Processing frame {}", currentFrame);

            logger.debug(
                    "Elements in Task Queue: {}, Data waiting for next frame: {}",
                    taskQueue.size(),
                    this.dataForFutureScope.size()
            );

            //Calculation Stage
            try {
                performCalculationStage(taskQueue, currentFrame.getIsolatedMetrics());
            } catch (InterruptedException e) {
                //TODO: handle interruption correctly.
                logger.debug(e);
                break;
            }

            logger.debug("Finished calculation phase");

            final Map<Class, Map<String, MetricState>> dataMap = Maps.newHashMap();
            performMetricCollection(currentFrame, dataMap);

            logger.debug("Finished data extraction phase");

            //Collection stage
            final List<ProducerMetric.Produce> produceList = Lists.newLinkedList();
            try {
                performCollectionStage(dataMap, currentFrame, produceList);
            } catch (InterruptedException e) {
                //TODO: handle interruption correctly.
                logger.debug(e);
                break;
            }

            logger.debug("Finished collection phase, produce created: {}", produceList.size());

            //Partition produce for next frame or next scopes, depending on the produce
            final Map<String, List<Object>> nextFrameExecutionData = Maps.newHashMap();
            prepareProduceForNextFrame(nextFrameExecutionData, produceList, currentScope);

            logger.debug("Produce partitioned.");

            currentFrame = currentFrame.getNextFrame();
            if (currentFrame == null && scopeIterator.hasNext()) {
                currentScope = scopeIterator.next();
                currentFrame = executionPlan.getPipelineFrame(currentScope);

                checkState(
                        nextFrameExecutionData.isEmpty(),
                        "No produce should go directly cross-scope, but '%s' did.",
                        nextFrameExecutionData
                );

                //Prepare the containers and task set for the next scope.
                final Map<String, List<Object>> deferredData = this.dataForFutureScope.row(currentScope);
                prepareEventBuses(
                        deferredData.keySet(),
                        executionPlan.getHandlerMap(currentScope),
                        this.stateContainers
                );
                nextFrameExecutionData.putAll(deferredData);
                deferredData.clear();

                logger.debug("State containers and tasks added for next scope.");
            }

            prepareCalculatorsForNextFrame(taskQueue, nextFrameExecutionData);

            logger.debug("Tasks for next frame prepared.");
        }

        executorPool.shutdown();

        executionPlan.onFinish();
    }

    private void prepareCalculatorsForNextFrame(
            final Queue<Pair<EventBus, Runnable>> taskQueue,
            final Map<String, List<Object>> executionData
    ) {
        for (final Map.Entry<String, List<Object>> entry : executionData.entrySet()) {
            final EventBus eBus = this.stateContainers.get(entry.getKey());
            //TODO: if eBus == null, try to fix it....
            taskQueue.add(new Pair<EventBus, Runnable>(eBus, new DataListDispatcher(eBus, entry.getValue())));
        }
    }

    private void prepareProduceForNextFrame(
            final Map<String, List<Object>> executionMap,
            final List<ProducerMetric.Produce> produceList,
            final MetricScope currentScope
    ) {
        for (final ProducerMetric.Produce produce : produceList) {
            final Map<String, List<Object>> targetMap;
            if (produce.getScope() == currentScope) {
                targetMap = executionMap;
            } else {
                targetMap = this.dataForFutureScope.row(produce.getScope());
            }

            final List<Object> todoList;
            if (targetMap.containsKey(produce.getTarget())) {
                todoList = targetMap.get(produce.getTarget());
            } else {
                todoList = Lists.newLinkedList();
                targetMap.put(produce.getTarget(), todoList);
            }

            todoList.add(produce.getProduce());
        }
    }

    private void performCollectionStage(
            final Map<Class, Map<String, MetricState>> dataMap,
            final PipelineFrame currentFrame,
            final List<ProducerMetric.Produce> produceOutput
    ) throws InterruptedException {
        final List<SharedMetric> sharedMetrics = currentFrame.getSharedMetrics();
        final List<ProducerMetric> producerMetrics = currentFrame.getProducerMetrics();

        final CountDownLatch latch = createCountdownLatch(sharedMetrics.size() + producerMetrics.size());

        //TODO: use Pair to link Futures to the metrics, for logging purposes
        final List<Future<List<MetricResult>>> futureResults = Lists.newLinkedList();
        final List<Future<List<ProducerMetric.Produce>>> futureProduce = Lists.newLinkedList();

        for (final SharedMetric metric : sharedMetrics) {
            final Map<String, MetricState> data = dataMap.get(metric.getClass());
            futureResults.add(this.executorPool.submit(CollectionStageTask.forSharedMetric(metric, data, latch)));
        }

        for (final ProducerMetric metric : producerMetrics) {
            final Map<String, MetricState> data = dataMap.get(metric.getClass());
            futureProduce.add(this.executorPool.submit(CollectionStageTask.forProducer(metric, data, latch)));
        }

        //Await completion of async collection.
        latch.await();

        //Collection Results
        final List<MetricResult> results = Lists.newLinkedList();
        for (final Future<List<MetricResult>> fResult : futureResults) {
            try {
                final List<MetricResult> resList = fResult.get();
                if (resList != null) {
                    results.addAll(resList);
                } else {
                    //TODO: log invalid metric
                }
            } catch (ExecutionException e) {
                //TODO: how to handle errors here (do they even happen?)
                logger.warn("Exception getting results", e);
            }
        }

        //Send off the results
        if (!results.isEmpty())
            this.resultsCallback.apply(results);

        //Collect and return produce.
        for (final Future<List<ProducerMetric.Produce>> fProduce : futureProduce) {
            try {
                final List<ProducerMetric.Produce> prodList = fProduce.get();
                if (prodList != null) {
                    produceOutput.addAll(prodList);
                } else {
                    //TODO: log invalid metric
                }
            } catch (ExecutionException e) {
                //TODO: how to handle errors here (do they even happen?)
                logger.warn("Exception getting results", e);
            }
        }
    }

    private void performMetricCollection(
            final PipelineFrame currentFrame,
            final Map<Class, Map<String, MetricState>> dataMap
    ) {
        final Table<String, Class, MetricState> dataTmp = HashBasedTable.create();

        final List<Class> classFilter = Lists.newLinkedList();
        classFilter.addAll(Lists.transform(currentFrame.getSharedMetrics(), OBJECT_GETCLASS));
        classFilter.addAll(Lists.transform(currentFrame.getProducerMetrics(), OBJECT_GETCLASS));

        if (classFilter.isEmpty()) return; //No shared/producer metrics in this frame.

        //Extract data for Shared + Produced metrics
        for (final Map.Entry<String, EventBus> entry : this.stateContainers.entrySet()) {
            dataTmp.row(entry.getKey()).putAll(entry.getValue().extractData(classFilter));
        }

        //Output the data
        dataMap.putAll(Tables.transpose(dataTmp).rowMap());
    }

    private void performCalculationStage(
            final Queue<Pair<EventBus, Runnable>> taskQueue,
            final List<IsolatedMetric> isolatedMetrics
    ) throws InterruptedException {
        final CountDownLatch calculationStageLatch = createCountdownLatch(taskQueue.size());

        while (!taskQueue.isEmpty()) {
            final Pair<EventBus, Runnable> task = taskQueue.poll();
            this.executorPool.submit(new CalculationStageTask(
                    calculationStageLatch,
                    task.second,
                    task.first,
                    isolatedMetrics,
                    this.resultsCallback
            ));
        }

        calculationStageLatch.await();
    }
}
