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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Metric calculator for the Index of Package Services Cohesion (IPSC)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IPSC extends SharedMetric {

    public IPSC() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final Set<String> packs = pack.ClientsP();
        final int pSize = packs.size();
        double acc = 0;
        for (final String packageName : packs) {
            acc += CScohesion(pack, pack.getPackageByName(packageName));
        }
        state.setValue("IPSC-p", pSize != 0 ? acc / pSize : 1.0);
    }

    public double CScohesion(final PackageUnit p, final PackageUnit q) {
        final Set<String> packs = p.ClientsP();
        double acc = 0;
        for (final String packageName : packs) {
            acc += SP(p, q, p.getPackageByName(packageName));
        }
        return acc / packs.size();
    }

    public double SP(final PackageUnit p, final PackageUnit q, final PackageUnit k) {
        final Set<String> pq = CS(p, q);
        final Sets.SetView<String> lambda = Sets.intersection(pq, CS(p, k));
        if (lambda.size() != 0) {
            return ((double) lambda.size()) / pq.size();
        } else {
            return 1;
        }
    }

    public Set<String> CS(final PackageUnit p, final PackageUnit q) {
        return Sets.intersection(p.InInt(), q.ProvidersC());
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states) {
        final List<MetricResult> results = Lists.newLinkedList();

        final AtomicDouble acc = new AtomicDouble(0);

        FluentIterable.from(states.entrySet())
                .transform(new Function<Map.Entry<String, MetricState>, MetricResult>() {
                    @Override
                    public MetricResult apply(final Map.Entry<String, MetricState> entry) {
                        final double result = entry.getValue().getValue("IPSC-p");

                        acc.addAndGet(result);

                        return new MetricResult(
                                entry.getKey(),
                                IPSC.this,
                                result
                        );
                    }
                })
                .copyInto(results);

        //TODO: fix for different collections
        results.add(new MetricResult(
                "Cptn. Placeholder",
                IPSC.class,
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
