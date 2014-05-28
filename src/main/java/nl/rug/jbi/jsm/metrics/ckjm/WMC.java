package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.base.Supplier;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

/**
 * Metric calculator for the Weighted Method per Class (WMC)
 * Please be aware that this implementation sets all method complexity to 1.
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class WMC extends IsolatedMetric {
    private static final Supplier<Integer> ZERO_INT = new Supplier<Integer>() {
        @Override
        public Integer get() {
            return 0;
        }
    };

    public WMC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition ignored) {
        final int val = state.getValue("method-num", ZERO_INT);
        state.setValue("method-num", val + 1);
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        return new MetricResult(
                identifier,
                this,
                state.getValue("method-num", ZERO_INT)
        );
    }
}
