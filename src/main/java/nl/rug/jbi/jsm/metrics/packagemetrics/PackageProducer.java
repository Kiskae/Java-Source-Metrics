package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.bcel.JavaClass;
import nl.rug.jbi.jsm.bcel.Method;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Map;

public class PackageProducer implements ProducerMetric<Package> {

    @Subscribe
    public void onClass(final MetricState state, final JavaClass c) {

    }

    @Subscribe
    public void onMethod(final MetricState state, final Method m) {

    }

    @Override
    public List<Produce<Package>> getProduce(Map<String, MetricState> states) {
        return ImmutableList.of();
    }

    @Override
    public Class<Package> getProducedClass() {
        return Package.class;
    }
}
