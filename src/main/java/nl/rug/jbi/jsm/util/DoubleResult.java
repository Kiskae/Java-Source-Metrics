package nl.rug.jbi.jsm.util;

import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;

public class DoubleResult extends MetricResult {

    private final double result;

    public DoubleResult(final String identifier, final Class<? extends BaseMetric> metricClass,
                        final MetricScope scope, final double result) {
        super(identifier, metricClass, scope);

        this.result = result;
    }

    @Override
    public double getValue() {
        return this.result;
    }
}
