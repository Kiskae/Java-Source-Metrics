package nl.rug.jbi.jsm.core.execution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.event.EventBus;
import nl.rug.jbi.jsm.core.pipeline.HandlerMap;
import nl.rug.jbi.jsm.core.pipeline.PipelineFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class ControllerThread extends Thread {
    private final static Logger logger = LogManager.getLogger(ControllerThread.class);
    private final static AtomicInteger UNIQUE_EXECUTION_ID = new AtomicInteger(0);
    private final static List<MetricScope> SCOPE_EXECUTION_ORDER = Lists.newArrayList(
            MetricScope.CLASS,
            MetricScope.PACKAGE,
            MetricScope.COLLECTION
    );

    private final PipelineExecutor executionPlan;
    private final ExecutorService executorPool = Executors.newFixedThreadPool(6, new ThreadFactory() {
        private final AtomicInteger executionId = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format("JSM Execution Thread #%d", executionId.getAndIncrement()));
        }
    });
    private final Map<String, EventBus> stateContainers = Maps.newHashMap();

    public ControllerThread(final PipelineExecutor executionPlan, final Set<String> classNames) {
        super(String.format("JSM Controller Thread #%d", ControllerThread.UNIQUE_EXECUTION_ID.getAndIncrement()));
        this.executionPlan = executionPlan;

        final HandlerMap classHandlerMap = this.executionPlan.getHandlerMap(MetricScope.CLASS);
        for (final String className : classNames) {
            final EventBus eBus = new EventBus(className, classHandlerMap);
            stateContainers.put(className, eBus);
        }
    }

    private static CountDownLatch createCountdownLatch(final int childTaskNum) {
        return new CountDownLatch(childTaskNum);
    }

    @Override
    public void run() {
        final Iterator<MetricScope> scopeIterator = SCOPE_EXECUTION_ORDER.iterator();

        MetricScope currentScope = scopeIterator.next();
        PipelineFrame currentFrame = executionPlan.getPipelineFrame(currentScope);

        //TODO:
        //- Execute data distribution runnables left by the previous frame (class visitors or data distributors)
        //- Process IsolatedMetrics immediately, collect SharedMetrics + Producers until all threads are done
        //- Process SharedMetrics + Producers
        //- Producers create a new set of data, if cross-scope, rebuild EventBus table.
        //- Execute next frame if exists.
        //- If not, execute final callback.

        while (currentFrame != null) {
            logger.debug("Processing frame {}", currentFrame);
            //TODO: launch stored data with eventBusses
            //TODO: await on countdownlatch
            //TODO: process shared metrics
            //TODO: pass results to frontend
            //TODO: prepare producer data for next frame

            currentFrame = currentFrame.getNextFrame();
            if (currentFrame == null && scopeIterator.hasNext()) {
                currentScope = scopeIterator.next();
                currentFrame = executionPlan.getPipelineFrame(currentScope);
                //TODO: process stored produced data and prepare new EventBusses
            }
        }

        try {
            //TODO: shut down more robustly.
            executorPool.shutdown();
            executorPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            logger.debug(e);
        }

        executionPlan.onFinish();
    }
}
