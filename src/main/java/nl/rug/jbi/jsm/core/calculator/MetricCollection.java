package nl.rug.jbi.jsm.core.calculator;

import java.util.Set;

/**
 * Convenience interface to register a set of metrics at once.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public interface MetricCollection {

    /**
     * @return Set of metrics to be registered.
     */
    public Set<BaseMetric> getMetrics();
}
