package nl.rug.jbi.jsm.metrics;

import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;

/**
 * @param <R>
 */
public interface IsolatedMetric<R extends MetricResult> {

    public R getResult(final MetricState state);
}
