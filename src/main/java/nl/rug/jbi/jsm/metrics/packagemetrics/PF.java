package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
 * Metric calculator for the Index of Package Goal Focus (PF)
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class PF extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(PF.class);

    public PF() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("PF-p", calcPF(pack));
    }

    private double calcPF(final PackageUnit pack) {
        final Set<String> usedPackages = pack.ClientsP();
        if (!usedPackages.isEmpty()) {
            double res = 0.0;
            for (final String pi : usedPackages) {
                final PackageUnit pu = pack.getPackageByName(pi);
                res += Role(pack, pu);
            }
            return res / usedPackages.size();
        } else {
            return 1.0;
        }
    }

    private double Role(final PackageUnit p, final PackageUnit q) {
        final double InIntPQ = Sets.intersection(p.InInt(), q.ProvidersC()).size();
        return InIntPQ / p.InInt().size();
    }

    @Override
    public List<MetricResult> getResults(final Map<String, MetricState> states, final int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "PF: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final List<MetricResult> results = Lists.newLinkedList();
        final CollectionAccumulator collectionData = new CollectionAccumulator();

        for (final Map.Entry<String, MetricState> entry : states.entrySet()) {
            final String collectionName = entry.getValue().getValue("Collection");
            final double result = entry.getValue().getValue("PF-p");

            results.add(new MetricResult(
                    entry.getKey(),
                    this,
                    result
            ));

            collectionData.getOrSet(collectionName, "PackageCount", DEF_ATOMIC_INT).incrementAndGet();
            collectionData.getOrSet(collectionName, "result", DEF_ATOMIC_DOUBLE).addAndGet(result);
        }

        for (final Map.Entry<String, Map<String, Object>> entry : collectionData.getEntrySetByCollection()) {
            final double result = ((AtomicDouble) entry.getValue().get("result")).doubleValue();
            final int packageCount = ((AtomicInteger) entry.getValue().get("PackageCount")).intValue();

            results.add(new MetricResult(
                    entry.getKey(),
                    PF.class,
                    MetricScope.COLLECTION,
                    result / packageCount
            ));
        }

        return results;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.PACKAGE, MetricScope.COLLECTION);
    }
}
