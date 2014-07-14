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
import org.apache.bcel.util.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Implementation of the execution of the {@link nl.rug.jbi.jsm.core.pipeline.Pipeline}.
 * The execution is performed by performing the following loop.
 * <ul>
 * <li>{@link #performCalculationStage(java.util.Queue, java.util.List)}</li>
 * <li>{@link #performMetricCollection(nl.rug.jbi.jsm.core.pipeline.PipelineFrame, java.util.Map)}</li>
 * <li>{@link #performCollectionStage(java.util.Map, nl.rug.jbi.jsm.core.pipeline.PipelineFrame, java.util.List)}</li>
 * <li>{@link #prepareProduceForNextFrame(java.util.Map, java.util.List, nl.rug.jbi.jsm.core.calculator.MetricScope)}</li>
 * <li>If end of frame, merge data for the next scope into the deliverables for the next frame.</li>
 * <li>{@link #prepareCalculatorsForNextFrame(java.util.Queue, java.util.Map, nl.rug.jbi.jsm.core.pipeline.HandlerMap)}</li>
 * <li>If there is no next frame, exit loop.</li>
 * </ul>
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
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
    private final int executionId = UNIQUE_EXECUTION_ID.incrementAndGet();
    private final ExecutorService executorPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private final AtomicInteger executionId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format(
                            "JSM Execution Thread #%d/%d",
                            ControllerThread.this.executionId,
                            executionId.getAndIncrement()
                    ));
                }
            }
    );
    private final PipelineExecutor executionPlan;
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
        super();
        this.setName(String.format("JSM Controller Thread #%d", this.executionId));

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

        //Create the base set of modifiers for the first frame, based on the classes that need to be inspected.
        for (final Map.Entry<String, EventBus> entry : this.stateContainers.entrySet()) {
            final String className = entry.getKey();
            final EventBus eBus = entry.getValue();

            taskQueue.add(new Pair<EventBus, Runnable>(
                    eBus,
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cvFactory.createClassVisitor(repo.loadClass(className), eBus).run();
                            } catch (ClassNotFoundException e) {
                                logger.error("Failed to load '{}', it will not be evaluated.", e);
                            }
                        }
                    }
            ));
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
                logger.warn("Execution interrupted, stopping evaluation.", e);
                break;
            }

            logger.debug("Finished calculation phase");

            final Map<Class, Map<String, MetricState>> dataMap = Maps.newHashMap();
            performMetricCollection(currentFrame, dataMap);

            logger.debug("Finished data extraction phase");

            //Collection stage
            final List<ProducerMetric.Produce> produceList = Lists.newLinkedList();
            try {
                //If there is no data, skip this step.
                if (!dataMap.isEmpty()) {
                    performCollectionStage(dataMap, currentFrame, produceList);
                }
            } catch (InterruptedException e) {
                logger.warn("Execution interrupted, stopping evaluation.", e);
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

            //Prepare tasks containing data for the next frame.
            prepareCalculatorsForNextFrame(
                    taskQueue,
                    nextFrameExecutionData,
                    this.executionPlan.getHandlerMap(currentScope)
            );

            logger.debug("Tasks for next frame prepared.");
        }

        executorPool.shutdown();
        repo.clear();

        executionPlan.onFinish();
    }

    /**
     * Step that gets executed at the end of the frame, it will turn the produce produced by the producers into a set of
     * {@link nl.rug.jbi.jsm.core.execution.DataListDispatcher} for execution in the next frame.
     *
     * @see nl.rug.jbi.jsm.core.execution.DataListDispatcher
     */
    private void prepareCalculatorsForNextFrame(
            final Queue<Pair<EventBus, Runnable>> taskQueue,
            final Map<String, List<Object>> executionData,
            final HandlerMap hMap
    ) {
        for (final Map.Entry<String, List<Object>> entry : executionData.entrySet()) {
            final EventBus eBus;
            if (this.stateContainers.containsKey(entry.getKey())) {
                eBus = this.stateContainers.get(entry.getKey());
            } else {
                logger.warn(
                        "Request for undefined identifier, might indicate that " +
                                "a producer is returning wrong identifiers: '{}'",
                        entry.getKey()
                );

                eBus = new EventBus(entry.getKey(), hMap);
                this.stateContainers.put(entry.getKey(), eBus);
            }
            taskQueue.add(new Pair<EventBus, Runnable>(eBus, new DataListDispatcher(eBus, entry.getValue())));
        }
    }

    /**
     * Processes the produce of {@link #performCollectionStage(java.util.Map, nl.rug.jbi.jsm.core.pipeline.PipelineFrame, java.util.List)}
     * by splitting the produce based on whether it needs to be delivered in the current frame or a future frame.
     * The produce meant for the current frame gets prepared to be sent to
     * {@link #prepareCalculatorsForNextFrame(java.util.Queue, java.util.Map, nl.rug.jbi.jsm.core.pipeline.HandlerMap)}
     * while the other produce gets stored based on the scope its meant to be delivered in.
     */
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

    /**
     * Based on the data collected by {@link #performMetricCollection(nl.rug.jbi.jsm.core.pipeline.PipelineFrame, java.util.Map)}
     * calculate both results and produce of declared {@link nl.rug.jbi.jsm.core.calculator.SharedMetric} and
     * {@link nl.rug.jbi.jsm.core.calculator.ProducerMetric}.
     * The results from the shared metrics will be sent off to the frontend while the produce gets returned to the loop
     * for further processing.
     *
     * @see nl.rug.jbi.jsm.core.execution.CollectionStageTask#forSharedMetric(nl.rug.jbi.jsm.core.calculator.SharedMetric, java.util.Map, java.util.concurrent.CountDownLatch)
     * @see nl.rug.jbi.jsm.core.execution.CollectionStageTask#forProducer(nl.rug.jbi.jsm.core.calculator.ProducerMetric, java.util.Map, java.util.concurrent.CountDownLatch)
     */
    private void performCollectionStage(
            final Map<Class, Map<String, MetricState>> dataMap,
            final PipelineFrame currentFrame,
            final List<ProducerMetric.Produce> produceOutput
    ) throws InterruptedException {
        final List<SharedMetric> sharedMetrics = currentFrame.getSharedMetrics();
        final List<ProducerMetric> producerMetrics = currentFrame.getProducerMetrics();

        final CountDownLatch latch = createCountdownLatch(sharedMetrics.size() + producerMetrics.size());

        final List<Pair<SharedMetric, Future<List<MetricResult>>>> futureResults = Lists.newLinkedList();
        final List<Pair<ProducerMetric, Future<List<ProducerMetric.Produce>>>> futureProduce = Lists.newLinkedList();

        //Begin execution of Shared Metric collection
        for (final SharedMetric metric : sharedMetrics) {
            final Map<String, MetricState> data = dataMap.get(metric.getClass());
            futureResults.add(new Pair<SharedMetric, Future<List<MetricResult>>>(
                    metric,
                    this.executorPool.submit(CollectionStageTask.forSharedMetric(metric, data, latch))
            ));
        }

        //Begin execution of Producer collection
        for (final ProducerMetric metric : producerMetrics) {
            final Map<String, MetricState> data = dataMap.get(metric.getClass());
            futureProduce.add(new Pair<ProducerMetric, Future<List<ProducerMetric.Produce>>>(
                            metric,
                            this.executorPool.submit(CollectionStageTask.forProducer(metric, data, latch)))
            );
        }

        //Await completion of async collection.
        latch.await();

        //Collection Results
        final List<MetricResult> results = Lists.newLinkedList();
        for (final Pair<SharedMetric, Future<List<MetricResult>>> fResult : futureResults) {
            try {
                final List<MetricResult> resList = fResult.second.get();
                if (resList != null) {
                    results.addAll(resList);
                } else {
                    logger.warn("'{}' returned null as a list of results.", fResult.first.getClass().getName());
                }
            } catch (ExecutionException e) {
                logger.warn(new ParameterizedMessage(
                        "Exception occurred whilst calculating results for '{}': {}",
                        fResult.first.getClass().getName(),
                        e.getCause().getMessage()
                ), e);
            }
        }

        //Send off the results
        if (!results.isEmpty())
            this.resultsCallback.apply(results);

        //Collect and return produce.
        for (final Pair<ProducerMetric, Future<List<ProducerMetric.Produce>>> fProduce : futureProduce) {
            try {
                final List<ProducerMetric.Produce> prodList = fProduce.second.get();
                if (prodList != null) {
                    produceOutput.addAll(prodList);
                } else {
                    logger.warn("'{}' returned null as a list of produce.", fProduce.first.getClass().getName());
                }
            } catch (ExecutionException e) {
                logger.warn(new ParameterizedMessage(
                        "Exception occurred whilst calculating produce for '{}': {}",
                        fProduce.first.getClass().getName(),
                        e.getCause().getMessage()
                ), e);
            }
        }
    }

    /**
     * Collects the states from the individual targets, then transposes the results so they are mapped by metric
     * instead of by target.
     */
    private void performMetricCollection(
            final PipelineFrame currentFrame,
            final Map<Class, Map<String, MetricState>> outputMap
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
        outputMap.putAll(Tables.transpose(dataTmp).rowMap());
    }

    /**
     * Applies the set of modifiers left by the last frame and calculate the isolated metrics.
     * This stage happens in parallel within a {@link java.util.concurrent.ExecutorService}, this thread will block
     * until all individual processing is done.
     *
     * @see nl.rug.jbi.jsm.core.execution.CalculationStageTask
     */
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

        //Block until individual processes are complete.
        calculationStageLatch.await();
    }
}
