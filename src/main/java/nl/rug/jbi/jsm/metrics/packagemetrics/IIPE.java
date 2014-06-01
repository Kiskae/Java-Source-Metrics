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
 * Metric calculator for the Index of Inter-Package Extending (IIPE)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class IIPE extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IIPE.class);

    public IIPE() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("Extends", pack.ExtSum());
        state.setValue("ExternalExtends", pack.ExtC());
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.COLLECTION);
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IIPE: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final CollectionAccumulator collectionData = new CollectionAccumulator();

        for (final MetricState state : states.values()) {
            final String collectionName = state.getValue("Collection");
            final Set<String> ExtSumP = state.getValue("ExternalExtends");
            final Set<String> ExtSumC = state.getValue("Extends");

            collectionData.getOrSet(collectionName, "ExtSumP", DEF_SET_STRING).addAll(ExtSumP);
            collectionData.getOrSet(collectionName, "ExtSumC", DEF_SET_STRING).addAll(ExtSumC);
        }

        final List<MetricResult> results = Lists.newLinkedList();

        for (final Map.Entry<String, Map<String, Object>> entry : collectionData.getEntrySetByCollection()) {
            final double ExtSumP = ((Set) entry.getValue().get("ExtSumP")).size();
            final int ExtSumC = ((Set) entry.getValue().get("ExtSumC")).size();

            results.add(MetricResult.getResult(
                    entry.getKey(),
                    IIPE.class,
                    MetricScope.COLLECTION,
                    1 - (ExtSumC != 0 ? ExtSumP / ExtSumC : 0)
            ));
        }

        return results;
    }
}
