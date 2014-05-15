package nl.rug.jbi.jsm.core.calculator;

import java.util.List;
import java.util.Map;

/**
 * @param <R>
 */
public abstract class SharedMetric<R extends MetricResult> extends BaseMetric {

    public SharedMetric(final MetricScope scope) {
        super(scope);
    }

    public abstract List<R> getResults(final Map<String, MetricState> states);
}
