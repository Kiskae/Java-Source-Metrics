package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.FieldAccessInstr;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Metric calculator for the Lack of Cohesion in Methods (LCOM)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class LCOM extends IsolatedMetric {
    private final static Supplier<List<Set<String>>> METHOD_SET_DEFAULT = new Supplier<List<Set<String>>>() {
        @Override
        public List<Set<String>> get() {
            return Lists.newLinkedList();
        }
    };

    public LCOM() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition ignored) {
        final List<Set<String>> miStack = state.getValueOrCreate("mi", METHOD_SET_DEFAULT);
        /*
         * CKJM:
         * We use a Tree rather than a Hash to calculate the
         * intersection in O(n) instead of O(n*n).
         */
        miStack.add(new TreeSet<String>());
    }

    @Subscribe
    public void onFieldAccess(final MetricState state, final FieldAccessInstr instr) {
        if (instr.getClassName().equals(state.getIdentifier())) {
            final List<Set<String>> miStack = state.getValueOrCreate("mi", METHOD_SET_DEFAULT);
            //Add to bottom Set of the stack (current method because onMethod called before)
            miStack.get(miStack.size() - 1).add(instr.getFieldName());
        }
    }

    @Override
    public MetricResult getResult(final String identifier, final MetricState state) {
        final List<Set<String>> miStack = state.getValueOrCreate("mi", METHOD_SET_DEFAULT);

        int lcom = 0;
        for (int i = 0; i < miStack.size(); ++i) {
            for (int j = i + 1; j < miStack.size(); ++j) {
                if (Sets.intersection(miStack.get(i), miStack.get(j)).isEmpty()) {
                    ++lcom;
                } else {
                    --lcom;
                }
            }
        }
        return MetricResult.getResult(identifier, this, lcom > 0 ? lcom : 0);
    }
}
