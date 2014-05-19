package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.FieldAccessInstr;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.util.DefaultValue;
import nl.rug.jbi.jsm.util.DoubleResult;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LCOM extends IsolatedMetric {
    private final static DefaultValue<List<Set<String>>> METHOD_SET_DEFAULT = new DefaultValue<List<Set<String>>>() {
        @Override
        public List<Set<String>> getDefault() {
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
    public MetricResult getResult(String identifier, MetricState state) {
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
        return new DoubleResult(identifier, this, lcom > 0 ? lcom : 0);
    }
}
