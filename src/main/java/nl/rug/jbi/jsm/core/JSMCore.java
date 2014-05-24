package nl.rug.jbi.jsm.core;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.bcel.CompositeBCELClassLoader;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.execution.PipelineExecutor;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.core.pipeline.Pipeline;
import nl.rug.jbi.jsm.frontend.Frontend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Set;

public class JSMCore {
    private final static Logger logger = LogManager.getLogger(JSMCore.class);
    private final Pipeline pipe = new Pipeline();
    private volatile boolean running = false;

    public void registerMetric(final BaseMetric metric) throws MetricPreparationException {
        Preconditions.checkState(!this.running, "Cannot register new metrics while the core is already executing.");
        this.pipe.registerMetric(metric);
    }

    public void registerMetricCollection(final MetricCollection collection) throws MetricPreparationException {
        for (final BaseMetric metric : collection.getMetrics()) {
            this.registerMetric(metric);
        }
    }

    public List<Class> getMetricsForScope(final MetricScope scope) {
        return this.pipe.getMetricsForScope(scope);
    }

    public void process(final Frontend frontend, final Set<String> classNames, final URL... sources) {
        Preconditions.checkState(!this.running, "Processing already in process.");
        this.running = true;

        final CompositeBCELClassLoader ccl = new CompositeBCELClassLoader(sources);
        final PipelineExecutor executor = new PipelineExecutor(frontend, pipe, ccl, classNames);

        executor.setFinishCallback(new Runnable() {
            @Override
            public void run() {
                JSMCore.this.running = false;
                frontend.signalDone();
            }
        });

        executor.beginExecution();
    }
}
