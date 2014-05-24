package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.Lists;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Metric calculator for the Index of Package Changing Intent (IPCI)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IPCI extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IPCI.class);

    public IPCI() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("IPCI-ClientsP", pack.ClientsP().size());
        state.setValue("Collection", pack.getSourceIdentifier());
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IPCI: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final List<MetricResult> ret = Lists.newLinkedList();

        double accumulator = 0.0;

        for (final MetricState state : states.values()) {
            final double ClientsP = state.<Integer>getValue("IPCI-ClientsP").doubleValue();

            final double result = 1 - (ClientsP / (states.size() - 1));
            accumulator += result;

            ret.add(new MetricResult(state.getIdentifier(), this, result));
        }

        //TODO: fix for different collections
        ret.add(new MetricResult(
                "Cptn. Placeholder",
                IPCI.class,
                MetricScope.COLLECTION,
                accumulator / states.size()
        ));

        return ret;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.PACKAGE, MetricScope.COLLECTION);
    }
}
