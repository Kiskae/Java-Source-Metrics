package nl.rug.jbi.jsm.metrics.ckjm;

import nl.rug.jbi.jsm.bcel.Method;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DefaultValues;

public class WMC extends IsolatedMetric<WMC.Result> {

    @Subscribe
    public void methodListener(final MetricState state, final Method ignored) {
        final int val = state.getValue("method-num", DefaultValues.ZERO_INT);
        state.setValue("method-num", val + 1);
    }

    @Override
    public Result getResult(final MetricState state) {
        return new Result(state.getValue("method-num", DefaultValues.ZERO_INT));
    }

    public static class Result implements MetricResult {
        private final Integer value;

        public Result(final Integer value) {
            this.value = value;
        }

        @Override
        public double getValue() {
            return this.value.doubleValue();
        }
    }
}
