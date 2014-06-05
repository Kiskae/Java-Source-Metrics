package nl.rug.jbi.jsm.core.calculator;

import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract base class for the metrics.
 * This class should never be directly extended, but be extended through {@link nl.rug.jbi.jsm.core.calculator.IsolatedMetric}
 * or {@link nl.rug.jbi.jsm.core.calculator.SharedMetric}.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public abstract class BaseMetric {
    /**
     * Global Collection identifier, used when a result concerns ALL input, not just select parts of it.
     */
    public final static String GLOBAL_COLLECTION_IDENTIFIER = "<all>";

    private final MetricScope scope;

    BaseMetric(final MetricScope scope) {
        this.scope = checkNotNull(scope);
    }

    /**
     * Returns the scope in which this metric operates. This means receives events and gets evaluated for results.
     *
     * @return the operating scope of this metric.
     */
    public MetricScope getScope() {
        return this.scope;
    }

    /**
     * Returns all the scopes for which this metric produces results, meant to be overridden if metrics are calculated
     * for multiple scopes at once or a different scope than their operating scope.
     *
     * @return A set of MetricScopes that this metric calculates results for.
     */
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(this.scope);
    }
}
