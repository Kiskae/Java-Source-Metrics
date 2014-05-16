package nl.rug.jbi.jsm.frontend;

import nl.rug.jbi.jsm.core.calculator.MetricResult;

import java.util.List;

public interface Frontend {

    public void init();

    public void processResult(final List<MetricResult> resultList);

    public void signalDone();
}
