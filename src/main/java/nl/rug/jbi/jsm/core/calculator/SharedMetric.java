package nl.rug.jbi.jsm.core.calculator;

import java.util.List;
import java.util.Map;

/**
 * Base class for metrics which require data from sources other than their assigned source. In plain terms this means
 * metrics that cover a shared property like use by other classes. (Which only those classes can know)
 * <br>
 * Evaluation of these metrics happens in two parts. First the assigned source is evaluated in parallel to all other
 * sources, during this time interaction between workers is not thread-safe. Finally all the MetricStates get collected
 * and passed to {@link #getResults(java.util.Map, int)}, where the states can be merged and results calculated.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public abstract class SharedMetric extends BaseMetric {

    /**
     * @param scope The operating scope of this metric.
     */
    public SharedMetric(final MetricScope scope) {
        super(scope);
    }

    /**
     * Evaluates the results for all states calculated using this metric.
     *
     * @param states         Map of all unique sources and the states calculated from their data.
     * @param invalidMembers The number of sources for which calculation fails, it is up to the metric to decide
     *                       whether this means it cannot be accurately calculated or issue a warning.
     * @return A list of results for the given map of states, should NEVER return NULL. If there are no results
     * {@link com.google.common.collect.ImmutableList#of()} can be returned.
     */
    public abstract List<MetricResult> getResults(final Map<String, MetricState> states, int invalidMembers);
}
