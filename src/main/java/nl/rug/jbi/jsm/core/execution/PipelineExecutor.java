package nl.rug.jbi.jsm.core.execution;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.bcel.CompositeBCELClassLoader;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.pipeline.HandlerMap;
import nl.rug.jbi.jsm.core.pipeline.Pipeline;
import nl.rug.jbi.jsm.core.pipeline.PipelineFrame;
import nl.rug.jbi.jsm.frontend.Frontend;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Executor for a previously constructed {@link nl.rug.jbi.jsm.core.pipeline.Pipeline}. Execution happens completely
 * asynchronously and will call {@link nl.rug.jbi.jsm.frontend.Frontend#signalDone()}.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class PipelineExecutor {
    private final static Logger logger = LogManager.getLogger(PipelineExecutor.class);
    private final Frontend frontend;
    private final CompositeBCELClassLoader dataSource;
    private final Repository repo;

    private final Map<MetricScope, HandlerMap> handlerMap;
    private final Map<MetricScope, PipelineFrame> frameMap;

    private final ControllerThread controllerThread;
    private final ClassVisitorFactory cvFactory;
    private Runnable finishCallback = null;

    /**
     * Construct an executor for the given data and target
     *  @param frontend      Frontend callback for results.
     * @param executionPlan Pipeline describing the handlers and execution-frames.
     * @param dataSource    ClassLoader to build the {@link org.apache.bcel.util.ClassLoaderRepository} from.
     * @param classNames    Set of classes that need to be evaluated.
     * @param cvFactory     Factory to create class visitors for the initial data-set.
     */
    public PipelineExecutor(
            final Frontend frontend,
            final Pipeline executionPlan,
            final CompositeBCELClassLoader dataSource,
            final Set<String> classNames,
            final ClassVisitorFactory cvFactory) {
        this.frontend = frontend;
        this.dataSource = dataSource;
        this.repo = new ClassLoaderRepository(dataSource);
        this.handlerMap = executionPlan.getHandlerMaps();
        this.frameMap = executionPlan.getPipelineFrames();
        this.cvFactory = cvFactory;
        this.controllerThread = new ControllerThread(this, classNames);
    }

    HandlerMap getHandlerMap(final MetricScope scope) {
        return this.handlerMap.get(Preconditions.checkNotNull(scope));
    }

    PipelineFrame getPipelineFrame(final MetricScope scope) {
        return this.frameMap.get(Preconditions.checkNotNull(scope));
    }

    /**
     * Sets a callback that is to be executed once processing has been finished.
     *
     * @param finishCallback the callback to be executed.
     */
    public void setFinishCallback(final Runnable finishCallback) {
        this.finishCallback = finishCallback;
    }

    /**
     * Begin the asynchronous execution of this executor, this call will not block.
     */
    public void beginExecution() {
        this.controllerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception has caused execution to fail", e);
                PipelineExecutor.this.onFinish(); //Ensure the system doesn't get stuck.
            }
        });
        this.controllerThread.start();
    }

    ClassVisitorFactory getClassVisitorFactory() {
        return this.cvFactory;
    }

    /**
     * Sets the ClassVisitor factory for this executor, this needs to be set to use custom ClassVisitors.
     * It will default to a factory that creates {@link nl.rug.jbi.jsm.bcel.ClassVisitor}.
     *
     * @param cvFactory ClassVisitor factory
     * @deprecated Removed in favor of providing the factory during JSMCore creation, this method does nothing
     */
    @Deprecated
    public void setClassVisitorFactory(final ClassVisitorFactory cvFactory) {
        //NOOP
        //this.cvFactory = Preconditions.checkNotNull(cvFactory);
    }

    Repository getRepository() {
        return this.repo;
    }

    CompositeBCELClassLoader getDataSource() {
        return this.dataSource;
    }

    void processResult(final List<MetricResult> resultList) {
        this.frontend.processResult(resultList);
    }

    void onFinish() {
        if (this.finishCallback != null) {
            this.finishCallback.run();
        }
    }
}
