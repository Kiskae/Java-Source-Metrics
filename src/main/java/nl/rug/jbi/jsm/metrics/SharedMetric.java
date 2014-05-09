package nl.rug.jbi.jsm.metrics;

import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;

import java.util.List;

/**
 * @param <R>
 */
public interface SharedMetric<R extends MetricResult> {

    public List<R> getResults(final List<MetricState> states);
}
