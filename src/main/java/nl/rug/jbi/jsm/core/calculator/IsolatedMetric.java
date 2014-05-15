package nl.rug.jbi.jsm.core.calculator;


/**
 * @param <R>
 */
public abstract class IsolatedMetric<R extends MetricResult> extends BaseMetric {

    public IsolatedMetric(final MetricScope scope) {
        super(scope);
    }

    public abstract R getResult(final String identifier, final MetricState state);
}
