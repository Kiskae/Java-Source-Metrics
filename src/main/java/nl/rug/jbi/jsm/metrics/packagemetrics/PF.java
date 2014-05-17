package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;

import java.util.List;
import java.util.Map;

public class PF extends SharedMetric {

    public PF() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final Package pack) {

    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states) {
        return ImmutableList.of();
    }
}
