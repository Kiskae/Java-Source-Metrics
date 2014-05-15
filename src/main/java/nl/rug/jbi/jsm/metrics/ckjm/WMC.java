package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.MethodData;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DefaultValues;
import nl.rug.jbi.jsm.util.DoubleResult;

public class WMC extends IsolatedMetric<MetricResult> {

    public WMC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodData ignored) {
        final int val = state.getValue("method-num", DefaultValues.ZERO_INT);
        state.setValue("method-num", val + 1);
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return new DoubleResult(
                identifier,
                WMC.class,
                MetricScope.CLASS,
                state.getValue("method-num", DefaultValues.ZERO_INT)
        );
    }
}
