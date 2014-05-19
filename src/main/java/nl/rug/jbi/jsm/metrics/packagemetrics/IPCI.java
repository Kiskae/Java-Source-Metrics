package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

public class IPCI extends IsolatedMetric {
    public IPCI() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final double ClientsP = pack.ClientsP().size();
        final int packageCount = pack.getPackageCount() - 1;
        if (packageCount != 0) {
            state.setValue("IPCI-p", 1 - (ClientsP / packageCount));
        } else {
            state.setValue("IPCI-p", 1);
        }
    }

    @Override
    public MetricResult getResult(final String identifier, MetricState state) {
        final double result = state.getValue("IPCI-p");

        return new MetricResult(identifier, this) {
            @Override
            public String getValue() {
                return String.format("%.2f", result);
            }
        };
    }
}
