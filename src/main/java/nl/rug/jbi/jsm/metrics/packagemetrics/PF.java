package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.core.calculator.*;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;

import java.util.List;
import java.util.Map;

public class PF extends SharedMetric<MetricResult> {

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final Package pack) {

    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states) {
        return null;
    }
}
