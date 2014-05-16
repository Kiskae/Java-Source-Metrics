package nl.rug.jbi.jsm.core.calculator;


/**
 * @param <R>
 */
public abstract class IsolatedMetric extends BaseMetric {

    public IsolatedMetric(final MetricScope scope) {
        super(scope);
    }

    public abstract MetricResult getResult(final String identifier, final MetricState state);
}
