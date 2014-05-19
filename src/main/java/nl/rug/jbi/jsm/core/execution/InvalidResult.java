package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;

public class InvalidResult extends MetricResult {
    private final String msg;

    public InvalidResult(String identifier, BaseMetric m, String msg) {
        super(identifier, m);
        this.msg = msg;
    }

    @Override
    public String getValue() {
        return this.msg;
    }
}
