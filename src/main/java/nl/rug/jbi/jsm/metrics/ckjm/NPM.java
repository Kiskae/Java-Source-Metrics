package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DefaultValues;
import nl.rug.jbi.jsm.util.DoubleResult;

public class NPM extends IsolatedMetric {

    public NPM() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition method) {
        //Same as WMC, just only public methods.
        if (method.isPublic()) {
            final int val = state.getValue("method-num", DefaultValues.ZERO_INT);
            state.setValue("method-num", val + 1);
        }
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return new DoubleResult(
                identifier,
                this,
                state.getValue("method-num", DefaultValues.ZERO_INT)
        );
    }
}
