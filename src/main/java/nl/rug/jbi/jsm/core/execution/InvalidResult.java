package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;

class InvalidResult extends MetricResult {
    public InvalidResult(String identifier, BaseMetric m, String msg) {
        super(identifier, m.getClass(), m.getResultScopes().iterator().next(), msg);
    }
}
