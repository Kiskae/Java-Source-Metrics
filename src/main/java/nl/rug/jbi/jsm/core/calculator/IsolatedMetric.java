package nl.rug.jbi.jsm.core.calculator;

/**
 * Base class for metrics which can be calculated with data from just a single unique source.
 * Metrics that require data from other sources or affect the results for other sources should subclass
 * {@link nl.rug.jbi.jsm.core.calculator.SharedMetric}.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public abstract class IsolatedMetric extends BaseMetric {

    /**
     * @param scope The operating scope of this metric.
     */
    public IsolatedMetric(final MetricScope scope) {
        super(scope);
    }

    /**
     * Evaluates the result for this metric. This will happen once all the data this metric declares to require has
     * been delivered. Execution of this method will also happen independently of the state of other instances, so using
     * any data from any source other than the provided MetricState is not thread-safe.
     *
     * @param identifier Identifier of the unique source assigned to this execution, can be a class-name, package-name
     *                   or collection identifier.
     * @param state      Associated state that is mutated using declared {@link nl.rug.jbi.jsm.core.event.Subscribe}
     *                   methods in this metric.
     * @return The result of calculation of this metric, this should NEVER return NULL.
     */
    public abstract MetricResult getResult(final String identifier, final MetricState state);
}
