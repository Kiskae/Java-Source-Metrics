package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Metric calculator for the Number of Children (NOC)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class NOC extends SharedMetric {
    private final Logger logger = LogManager.getLogger(NOC.class);

    public NOC() {
        super(MetricScope.CLASS);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition jc) {
        state.setValue("superclass", jc.getSuperClass());
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "NOC: Unsuccessful calculation for {} classes(s), results might be inaccurate.",
                    invalidMembers
            );
        }

        final Map<String, Integer> nocMap = Maps.newHashMap();

        for (final String className : states.keySet()) {
            nocMap.put(className, 0);
        }

        for (final MetricState ms : states.values()) {
            final String superclass = ms.getValue("superclass");
            final Integer noc = nocMap.get(superclass);
            //If its not a class we're inspecting, ignore it.
            if (noc == null) continue;
            nocMap.put(superclass, noc + 1);
        }

        final List<MetricResult> ret = Lists.newLinkedList();

        for (final Map.Entry<String, Integer> entry : nocMap.entrySet()) {
            ret.add(MetricResult.getResult(entry.getKey(), this, entry.getValue()));
        }

        return ret;
    }
}
