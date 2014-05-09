package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.Subscribe;
import nl.rug.jbi.jsm.core.calculator.UsingProducer;
import nl.rug.jbi.jsm.metrics.IsolatedMetric;

public class PF implements IsolatedMetric<MetricResult> {

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final Object pack) {

    }

    @Override
    public MetricResult getResult(final MetricState state) {
        return null;
    }
}
