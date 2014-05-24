package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DefaultValues;

public class WMC extends IsolatedMetric {

    public WMC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition ignored) {
        final int val = state.getValue("method-num", DefaultValues.ZERO_INT);
        state.setValue("method-num", val + 1);
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return new MetricResult(
                identifier,
                this,
                state.getValue("method-num", DefaultValues.ZERO_INT)
        );
    }
}
