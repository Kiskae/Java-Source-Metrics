package nl.rug.jbi.jsm.core;

import nl.rug.jbi.jsm.bcel.CompositeBCELClassLoader;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.execution.PipelineExecutor;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.core.pipeline.Pipeline;
import nl.rug.jbi.jsm.frontend.Frontend;

import java.net.URL;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * This class serves to encapsulate the runtime interaction and execution of the JSM core.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public class JSMCore {
    private final Pipeline pipe = new Pipeline();
    private volatile boolean running = false;

    /**
     * Register a metric to be evaluated during execution.
     * The metric has to extend either {@link nl.rug.jbi.jsm.core.calculator.IsolatedMetric} or
     * {@link nl.rug.jbi.jsm.core.calculator.SharedMetric}.
     *
     * @param metric Metric to be evaluated
     * @throws MetricPreparationException      If there is an issue with the metric definition, please refer to the
     *                                         exception message for details.
     * @throws java.lang.IllegalStateException If the core is currently processing.
     */
    public void registerMetric(final BaseMetric metric) throws MetricPreparationException {
        checkNotNull(metric, "Metric cannot be NULL.");
        checkState(!this.running, "Cannot register new metrics while the core is already executing.");
        this.pipe.registerMetric(metric);
    }

    /**
     * Convenience metric to register a set of metrics, calls {@link #registerMetric(nl.rug.jbi.jsm.core.calculator.BaseMetric)}
     * for all metrics that are part of the collection.
     *
     * @param collection Collection of metrics to be evaluated
     * @throws MetricPreparationException      If there is an issue with the definition of one of the metrics, please
     *                                         refer to the exception message for details.
     * @throws java.lang.IllegalStateException If the core is currently processing.
     */
    public void registerMetricCollection(final MetricCollection collection) throws MetricPreparationException {
        checkNotNull(collection, "Collection cannot be NULL.");
        for (final BaseMetric metric : collection.getMetrics()) {
            this.registerMetric(metric);
        }
    }

    /**
     * Retrieve a list of metrics that are registered for a certain scope.
     *
     * @param scope MetricScope to retrieve metrics for.
     * @return A list of all metrics that have declared to yield results for the given scope.
     */
    public List<Class> getMetricsForScope(final MetricScope scope) {
        return this.pipe.getMetricsForScope(scope);
    }

    /**
     * @return Whether the core is currently executing asynchronously.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Begin asynchronous evaluation of the given classNames.
     * The core will call {@link nl.rug.jbi.jsm.frontend.Frontend#signalDone()} when asynchronous evaluation has
     * completed.
     *
     * @param frontend   Interface to receive results and be alerted of evaluation completion.
     * @param classNames List of classNames that are to be evaluated by the system, package and collection names will
     *                   be derived from this list.
     * @param sources    List of sources for the binary class files, classes that are not part of the classNames set
     *                   will not be evaluated.
     * @throws java.lang.IllegalStateException if this method is invoked while a previous execution is still going on.
     */
    public void process(final Frontend frontend, final Set<String> classNames, final URL... sources) {
        checkState(!this.running, "Processing already in process.");
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
