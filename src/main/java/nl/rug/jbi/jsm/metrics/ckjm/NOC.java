package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Map;

public class NOC extends SharedMetric {

    public NOC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition jc) {
        state.setValue("superclass", jc.getSuperClass());
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        //TODO: check invalidMembers, output warning if != 0
        final Map<String, Integer> nocMap = Maps.newHashMap();

        for (final String className : states.keySet()) {
            nocMap.put(className, 0);
        }

        for (final MetricState ms : states.values()) {
            final String superclass = ms.getValue("superclass");
            final Integer noc = nocMap.get(superclass);
            if (noc == null) continue;
            nocMap.put(superclass, noc + 1);
        }

        final List<MetricResult> ret = Lists.newLinkedList();

        for (final Map.Entry<String, Integer> entry : nocMap.entrySet()) {
            ret.add(new MetricResult(entry.getKey(), NOC.class, MetricScope.CLASS, entry.getValue()));
        }

        return ret;
    }
}
