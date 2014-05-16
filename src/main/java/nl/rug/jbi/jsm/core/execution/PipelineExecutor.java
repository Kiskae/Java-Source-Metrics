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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PipelineExecutor {
    private final Frontend frontend;
    private final CompositeBCELClassLoader dataSource;
    private final Repository repo;

    private final Map<MetricScope, HandlerMap> handlerMap;
    private final Map<MetricScope, PipelineFrame> frameMap;

    private final ControllerThread controllerThread;

    private Runnable finishCallback = null;
    private ClassVisitorFactory cvFactory = JSMClassVisitorFactory.INSTANCE;

    public PipelineExecutor(
            final Frontend frontend,
            final Pipeline pipe,
            final CompositeBCELClassLoader dataSource,
            final Set<String> classNames
    ) {
        this.frontend = frontend;
        this.dataSource = dataSource;
        this.repo = new ClassLoaderRepository(dataSource);
        this.handlerMap = pipe.getHandlerMaps();
        this.frameMap = pipe.getPipelineFrames();
        this.controllerThread = new ControllerThread(this, classNames);
    }

    public HandlerMap getHandlerMap(final MetricScope scope) {
        return this.handlerMap.get(Preconditions.checkNotNull(scope));
    }

    public PipelineFrame getPipelineFrame(final MetricScope scope) {
        return this.frameMap.get(Preconditions.checkNotNull(scope));
    }

    public void setFinishCallback(final Runnable finishCallback) {
        this.finishCallback = finishCallback;
    }

    public void beginExecution() {
        //TODO: custom exception handler
        this.controllerThread.start();
    }

    ClassVisitorFactory getClassVisitorFactory() {
        return this.cvFactory;
    }

    public void setClassVisitorFactory(final ClassVisitorFactory cvFactory) {
        this.cvFactory = Preconditions.checkNotNull(cvFactory);
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
