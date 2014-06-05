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
import java.util.concurrent.atomic.AtomicInteger;

import static nl.rug.jbi.jsm.metrics.packagemetrics.CollectionAccumulator.DEF_ATOMIC_DOUBLE;
import static nl.rug.jbi.jsm.metrics.packagemetrics.CollectionAccumulator.DEF_ATOMIC_INT;

/**
 * Metric calculator for the Index of Inter-Package Extending Diversion (IIPED)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class IIPED extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IIPED.class);

    public IIPED() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final int ExtC = pack.ExtC().size();
        final double result;
        if (ExtC != 0) {
            final double Ext = pack.Ext().size();
            result = (1 / Ext) * (1 - ((Ext - 1) / ExtC));
        } else {
            result = 1.0;
        }

        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("IIPED-p", result);
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IIPED: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final List<MetricResult> results = Lists.newLinkedList();
        final CollectionAccumulator collectionData = new CollectionAccumulator();

        for (final Map.Entry<String, MetricState> entry : states.entrySet()) {
            final String collectionName = entry.getValue().getValue("Collection");
            final double result = entry.getValue().<Number>getValue("IIPED-p").doubleValue();

            results.add(MetricResult.getResult(
                    entry.getKey(),
                    this,
                    result
            ));

            collectionData.getOrSet(collectionName, "PackageCount", DEF_ATOMIC_INT).incrementAndGet();
            collectionData.getOrSet(collectionName, "result", DEF_ATOMIC_DOUBLE).addAndGet(result);
        }

        double gResult = 0.0;
        for (final Map.Entry<String, Map<String, Object>> entry : collectionData.getEntrySetByCollection()) {
            final double result = ((AtomicDouble) entry.getValue().get("result")).doubleValue();
            final int packageCount = ((AtomicInteger) entry.getValue().get("PackageCount")).intValue();

            gResult += result;

            results.add(MetricResult.getResult(
                    entry.getKey(),
                    IIPED.class,
                    MetricScope.COLLECTION,
                    result / packageCount
            ));
        }

        results.add(MetricResult.getResult(
                GLOBAL_COLLECTION_IDENTIFIER,
                IIPED.class,
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
