package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: documentation
 */
public class MetricResult {
    private final String identifier;
    private final Class metricClass;
    private final MetricScope scope;
    private final Object value;

    /**
     * @param identifier
     * @param metricClass
     * @param scope
     * @param value
     */
    public MetricResult(
            final String identifier,
            final Class<? extends BaseMetric> metricClass,
            final MetricScope scope,
            final Object value
    ) {
        this.value = value;
        this.metricClass = checkNotNull(metricClass);
        this.identifier = checkNotNull(identifier);
        this.scope = checkNotNull(scope);
    }

    /**
     * @param identifier
     * @param metric
     * @param value
     */
    public MetricResult(final String identifier, final BaseMetric metric, final Object value) {
        this(identifier, metric.getClass(), metric.getScope(), value);
    }

    /**
     * @return
     */
    public final Class getMetricClass() {
        return this.metricClass;
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
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
    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", identifier)
                .add("metricClass", metricClass.getSimpleName())
                .add("result", this.getValue())
                .toString();
    }
}
