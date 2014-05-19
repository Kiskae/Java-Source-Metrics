package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.core.execution.InvalidResult;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PF extends SharedMetric {

    public PF() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
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
    public List<MetricResult> getResults(Map<String, MetricState> states) {
        return FluentIterable.from(states.keySet()).transform(new Function<String, MetricResult>() {
            @Override
            public MetricResult apply(String s) {
                return new InvalidResult(s, PF.this, "NYI, InInt");
            }
        }).toList();
    }
}
