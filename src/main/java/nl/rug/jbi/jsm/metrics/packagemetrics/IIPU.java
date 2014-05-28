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
import java.util.Set;

import static nl.rug.jbi.jsm.metrics.packagemetrics.CollectionAccumulator.DEF_SET_STRING;

/**
 * Metric calculator for the Index of Inter-Package Usage (IIPU)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class IIPU extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IIPU.class);

    public IIPU() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("Uses", pack.UsesSum());
        state.setValue("ExternalUses", pack.UsesC());
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.COLLECTION);
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IIPU: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final CollectionAccumulator collectionData = new CollectionAccumulator();

        for (final MetricState state : states.values()) {
            final String collectionName = state.getValue("Collection");
            final Set<String> UsesSumP = state.getValue("ExternalUses");
            final Set<String> UsesSumC = state.getValue("Uses");

            collectionData.getOrSet(collectionName, "UsesSumP", DEF_SET_STRING).addAll(UsesSumP);
            collectionData.getOrSet(collectionName, "UsesSumC", DEF_SET_STRING).addAll(UsesSumC);
        }

        final List<MetricResult> results = Lists.newLinkedList();

        for (final Map.Entry<String, Map<String, Object>> entry : collectionData.getEntrySetByCollection()) {
            final double UsesSumP = ((Set) entry.getValue().get("UsesSumP")).size();
            final int UsesSumC = ((Set) entry.getValue().get("UsesSumC")).size();

            results.add(new MetricResult(
                    entry.getKey(),
                    IIPU.class,
                    MetricScope.COLLECTION,
                    1 - (UsesSumC != 0 ? UsesSumP / UsesSumC : 0)
            ));
        }

        return results;
    }
}
