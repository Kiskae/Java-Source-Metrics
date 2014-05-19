package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

public class IIPUD extends IsolatedMetric {

    public IIPUD() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final int UsesC = pack.UsesC().size();
        final double result;
        if (UsesC != 0) {
            final double Uses = pack.Uses().size();
            result = (1 / Uses) * (1 - ((Uses - 1) / UsesC));
        } else {
            result = 1.0;
        }

        state.setValue("IIPUD-p", result);
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        final double result = state.getValue("IIPUD-p");

        return new MetricResult(identifier, this) {
            @Override
            public String getValue() {
                return String.format("%.2f", result);
            }
        };
    }
}
