package nl.rug.jbi.jsm.frontend;

import nl.rug.jbi.jsm.core.calculator.MetricResult;

public interface Frontend {

    public void init();

    public void processResult(final MetricResult result);
}
