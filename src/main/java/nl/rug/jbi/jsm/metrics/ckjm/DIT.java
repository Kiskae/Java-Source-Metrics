package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

/**
 * Metric calculator for the Depth of Inheritance Tree (DIT)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class DIT extends IsolatedMetric {

    public DIT() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition clazz) {
        state.setValue("dit-depth", clazz.getSuperClasses().size());
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return MetricResult.getResult(identifier, this, state.getValue("dit-depth"));
    }
}
