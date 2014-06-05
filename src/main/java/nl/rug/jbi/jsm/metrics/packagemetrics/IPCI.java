package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
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
import java.util.concurrent.atomic.AtomicInteger;

import static nl.rug.jbi.jsm.metrics.packagemetrics.CollectionAccumulator.DEF_ATOMIC_DOUBLE;
import static nl.rug.jbi.jsm.metrics.packagemetrics.CollectionAccumulator.DEF_ATOMIC_INT;

/**
 * Metric calculator for the Index of Package Changing Intent (IPCI)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class IPCI extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IPCI.class);

    public IPCI() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        //For this metric, clamp ClientsP to the set P in M. This is done because ClientsP can contain packages
        //that are not part of the same collection, resulting in a ClientsP that is larger than P, which results
        //in negative values for this metric.
        final Set<String> ClientsP = pack.filterSameCollectionP(pack.ClientsP());
        state.setValue("IPCI-ClientsP", ClientsP.size());
        state.setValue("Collection", pack.getSourceIdentifier());
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IPCI: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final List<MetricResult> results = Lists.newLinkedList();
        final CollectionAccumulator collectionData = new CollectionAccumulator();

        for (final Map.Entry<String, MetricState> entry : states.entrySet()) {
            final String collectionName = entry.getValue().getValue("Collection");

            //Increment packageCount for the collection it belongs to
            collectionData.getOrSet(collectionName, "PackageCount", DEF_ATOMIC_INT).incrementAndGet();
        }

        for (final Map.Entry<String, MetricState> entry : states.entrySet()) {
            final String collectionName = entry.getValue().getValue("Collection");
            final double ClientsP = ((Number) entry.getValue().getValue("IPCI-ClientsP")).doubleValue();
            final int packageCount = collectionData.getOrSet(collectionName, "PackageCount", DEF_ATOMIC_INT).intValue();

            final double result = 1 - (ClientsP / (packageCount - 1));
            results.add(MetricResult.getResult(
                    entry.getKey(),
                    this,
                    result
            ));

            collectionData.getOrSet(collectionName, "result", DEF_ATOMIC_DOUBLE).addAndGet(result);
        }

        double gResult = 0.0;
        for (final Map.Entry<String, Map<String, Object>> entry : collectionData.getEntrySetByCollection()) {
            final double result = ((AtomicDouble) entry.getValue().get("result")).doubleValue();
            final int packageCount = ((AtomicInteger) entry.getValue().get("PackageCount")).intValue();

            gResult += result;

            results.add(MetricResult.getResult(
                    entry.getKey(),
                    IPCI.class,
                    MetricScope.COLLECTION,
                    result / packageCount
            ));
        }

        results.add(MetricResult.getResult(
                GLOBAL_COLLECTION_IDENTIFIER,
                IPCI.class,
                MetricScope.COLLECTION,
                gResult / states.size()
        ));

        return results;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.PACKAGE, MetricScope.COLLECTION);
    }
}
