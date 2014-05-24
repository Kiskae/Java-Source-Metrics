package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

public class DIT extends IsolatedMetric {

    public DIT() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition clazz) {
        state.setValue("dit-depth", clazz.getSuperClasses().size());
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        return new MetricResult(identifier, this, state.getValue("dit-depth"));
    }
}
