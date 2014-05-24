package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
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

/**
 * Metric calculator for the Index of Package Goal Focus (PF)
 *
 * @author David van Leusen
 * @since 1.0
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

    private double Role(PackageUnit p, PackageUnit q) {
        final double InIntPQ = Sets.intersection(p.InInt(), q.ProvidersC()).size();
        return InIntPQ / p.InInt().size();
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "PF: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final List<MetricResult> results = Lists.newLinkedList();

        final AtomicDouble acc = new AtomicDouble(0);

        FluentIterable.from(states.entrySet())
                .transform(new Function<Map.Entry<String, MetricState>, MetricResult>() {
                    @Override
                    public MetricResult apply(Map.Entry<String, MetricState> entry) {
                        final double pfResult = entry.getValue().getValue("PF-p");

                        acc.addAndGet(pfResult);

                        return new MetricResult(entry.getKey(), PF.this, pfResult);
                    }
                })
                .copyInto(results);

        //TODO: fix for different collections
        results.add(new MetricResult(
                "Cptn. Placeholder",
                PF.class,
                MetricScope.COLLECTION,
                acc.doubleValue() / states.size()
        ));

        return results;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.PACKAGE, MetricScope.COLLECTION);
    }
}
