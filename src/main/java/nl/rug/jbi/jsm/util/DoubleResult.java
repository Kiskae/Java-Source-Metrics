package nl.rug.jbi.jsm.util;

import com.google.common.base.Objects;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;

public class DoubleResult extends MetricResult<Double> {

    private final double result;

    public DoubleResult(final String identifier, final Class<? extends BaseMetric> metricClass,
                        final MetricScope scope, final double result) {
        super(identifier, metricClass, scope);
        this.result = result;
    }

    public DoubleResult(final String identifier, final BaseMetric metric, final double result) {
        super(identifier, metric.getClass(), metric.getScope());
        this.result = result;
    }

    @Override
    public Double getValue() {
        return this.result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", this.getIdentifier())
                .add("metric", this.getMetricClass().getName())
                .add("result", result)
                .toString();
    }
}
