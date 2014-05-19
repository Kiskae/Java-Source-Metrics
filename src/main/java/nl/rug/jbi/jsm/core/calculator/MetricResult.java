package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public abstract class MetricResult<R> {
    private final String identifier;
    private final Class<? extends BaseMetric> metricClass;
    private final MetricScope scope;

    public MetricResult(
            final String identifier,
            final Class<? extends BaseMetric> metricClass,
            final MetricScope scope
    ) {
        this.metricClass = Preconditions.checkNotNull(metricClass);
        this.identifier = Preconditions.checkNotNull(identifier);
        this.scope = Preconditions.checkNotNull(scope);
    }

    public MetricResult(final String identifier, final BaseMetric metric) {
        this(identifier, metric.getClass(), metric.getScope());
    }

    public final Class<? extends BaseMetric> getMetricClass() {
        return this.metricClass;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public MetricScope getScope() {
        return this.scope;
    }

    public abstract R getValue();

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", identifier)
                .add("metricClass", metricClass.getSimpleName())
                .add("result", this.getValue())
                .toString();
    }
}
