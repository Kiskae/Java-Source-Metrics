package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Result to be passed on to the {@link nl.rug.jbi.jsm.frontend.Frontend} for processing and possible display.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public class MetricResult {
    private final String identifier;
    private final Class metricClass;
    private final MetricScope scope;
    private final Object value;

    /**
     * Protected superclass for sub-classing.
     *
     * @param identifier  Identifier of the associated resource
     * @param metricClass Metric for which this is a result
     * @param scope       Scope that this metric belongs in.
     * @param value       The result value.
     * @throws java.lang.NullPointerException If anything but the value is NULL
     */
    protected MetricResult(
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
     * Convenient version of {@link #getResult(String, Class, MetricScope, Object)} which extracts the class and
     * scope from the given metric.
     *
     * @param identifier Identifier of the associated resource
     * @param metric     Metric for which this is a result
     * @param value      The result value.
     * @return The result for the given values.
     * @throws java.lang.NullPointerException If anything but the value is NULL
     */
    public static MetricResult getResult(final String identifier, final BaseMetric metric, final Object value) {
        return MetricResult.getResult(identifier, metric.getClass(), metric.getScope(), value);
    }

    /**
     * Construct a result for the given identifier-metric pair, in the given scope and with the given value.
     *
     * @param identifier  Identifier of the associated resource
     * @param metricClass Metric for which this is a result
     * @param scope       Scope that this metric belongs in.
     * @param value       The result value.
     * @return The result for the given values.
     * @throws java.lang.NullPointerException If anything but the value is NULL
     */
    public static MetricResult getResult(
            final String identifier,
            final Class<? extends BaseMetric> metricClass,
            final MetricScope scope,
            final Object value
    ) {
        return new MetricResult(identifier, metricClass, scope, value);
    }

    /**
     * @return The metric for which this is the result.
     */
    public final Class getMetricClass() {
        return this.metricClass;
    }

    /**
     * @return The associated resource that this result belongs to.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @return The scope that this result belongs in.
     */
    public MetricScope getScope() {
        return this.scope;
    }

    /**
     * @return The final result of the metric
     */
    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", this.identifier)
                .add("metricClass", this.metricClass.getSimpleName())
                .add("metricScope", this.scope)
                .add("result", this.getValue())
                .toString();
    }
}
