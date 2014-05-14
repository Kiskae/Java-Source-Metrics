package nl.rug.jbi.jsm.core.calculator;

import java.util.List;
import java.util.Map;

/**
 * @param <R>
 */
public abstract class SharedMetric<R extends MetricResult> extends BaseMetric<R> {

    public SharedMetric() {
        super();
    }

    public abstract List<R> getResults(final Map<String, MetricState> states);
}
