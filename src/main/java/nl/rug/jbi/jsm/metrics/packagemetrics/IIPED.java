package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

public class IIPED extends IsolatedMetric {

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

        state.setValue("IIPED-p", result);
    }

    @Override
    public MetricResult getResult(String identifier, MetricState state) {
        final double result = state.getValue("IIPED-p");

        return new MetricResult(identifier, this) {
            @Override
            public String getValue() {
                return String.format("%.2f", result);
            }
        };
    }
}
