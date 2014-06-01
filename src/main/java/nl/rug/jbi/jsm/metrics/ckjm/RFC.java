package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.InvokeMethodInstr;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.Set;

/**
 * Metric calculator for the Response for a Class (RFC)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class RFC extends IsolatedMetric {
    private final static Supplier<Set<String>> EMPTY_HASHSET_DEFAULT = new Supplier<Set<String>>() {
        @Override
        public Set<String> get() {
            return Sets.newHashSet();
        }
    };

    public RFC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition method) {
        final Set<String> responseSet = state.getValueOrCreate("responseSet", EMPTY_HASHSET_DEFAULT);

        /* CKJM: Measuring decision: A class's own methods contribute to its RFC */
        final String signature = String.format(
                "%s.%s(%s)",
                state.getIdentifier(),
                method.getMethodName(),
                Joiner.on(',').join(method.getExactArgumentTypes())
        );
        responseSet.add(signature);
    }

    @Subscribe
    public void onInvokeMethod(final MetricState state, final InvokeMethodInstr instr) {
        final Set<String> responseSet = state.getValueOrCreate("responseSet", EMPTY_HASHSET_DEFAULT);

        final String signature = String.format(
                "%s.%s(%s)",
                instr.getClassName(),
                instr.getMethodName(),
                Joiner.on(',').join(instr.getExactArgumentTypes())
        );
        responseSet.add(signature);
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        //ResponseSet size is all the different methods used by this class.
        return MetricResult.getResult(identifier, this, state.getValue("responseSet", EMPTY_HASHSET_DEFAULT).size());
    }
}
