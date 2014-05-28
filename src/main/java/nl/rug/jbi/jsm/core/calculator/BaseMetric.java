package nl.rug.jbi.jsm.core.calculator;

import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: documentation
 */
public class BaseMetric {
    private final MetricScope scope;

    BaseMetric(final MetricScope scope) {
        this.scope = checkNotNull(scope);
    }

    /**
     * @return
     */
    public MetricScope getScope() {
        return this.scope;
    }

    /**
     * @return
     */
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(this.scope);
    }
}
