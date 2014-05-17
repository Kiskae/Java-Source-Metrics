package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.JavaClassData;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DoubleResult;

public class DIT extends IsolatedMetric {

    public DIT() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassData clazz) {
        state.setValue("dit-depth", clazz.getSuperClasses().size());
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        final Integer DIT = state.getValue("dit-depth");
        return new DoubleResult(identifier, this, DIT.doubleValue());
    }
}
