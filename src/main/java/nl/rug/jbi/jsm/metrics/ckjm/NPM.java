package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.base.Supplier;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

/**
 * Metric calculator for the Number of Public Methods (NPM)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class NPM extends IsolatedMetric {
    private static final Supplier<Integer> ZERO_INT = new Supplier<Integer>() {
        @Override
        public Integer get() {
            return 0;
        }
    };

    public NPM() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition method) {
        //Same as WMC, just only public methods.
        if (method.isPublic()) {
            final int val = state.getValue("method-num", ZERO_INT);
            state.setValue("method-num", val + 1);
        }
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return MetricResult.getResult(
                identifier,
                this,
                state.getValue("method-num", ZERO_INT)
        );
    }
}
