package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Preconditions;

public class BaseMetric {
    private final MetricScope scope;

    public BaseMetric(final MetricScope scope) {
        this.scope = Preconditions.checkNotNull(scope);
    }

    public MetricScope getScope() {
        return this.scope;
    }
}
