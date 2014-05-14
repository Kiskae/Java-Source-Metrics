package nl.rug.jbi.jsm.core.calculator;


/**
 * @param <R>
 */
public abstract class IsolatedMetric<R extends MetricResult> extends BaseMetric<R> {

    public IsolatedMetric() {
        super();
    }

    public abstract R getResult(final MetricState state);
}
