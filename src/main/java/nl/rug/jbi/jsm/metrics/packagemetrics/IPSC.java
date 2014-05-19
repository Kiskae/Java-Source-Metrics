package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.core.execution.InvalidResult;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

import java.util.Set;

public class IPSC extends IsolatedMetric {

    public IPSC() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final Set<String> packs = pack.ClientsP();
        double acc = 0;
        for (final String packageName : packs) {
            acc += CScohesion(pack, pack.getPackageByName(packageName));
        }
        state.setValue("IPSC-p", acc);
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
        final int lambda = Sets.intersection(pq, CS(q, k)).size();
        if (lambda != 0) {
            return ((double) lambda) / pq.size();
        } else {
            return 1;
        }
    }

    public Set<String> CS(final PackageUnit p, final PackageUnit q) {
        return Sets.intersection(p.InInt(), q.ProvidersC());
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        return new InvalidResult(identifier, this, "NYI, InInt");
    }
}
