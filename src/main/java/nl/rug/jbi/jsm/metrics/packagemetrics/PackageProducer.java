package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.Lists;
import nl.rug.jbi.jsm.bcel.JavaClass;
import nl.rug.jbi.jsm.bcel.Method;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.Subscribe;
import nl.rug.jbi.jsm.metrics.ProducerMetric;

import java.util.List;

public class PackageProducer implements ProducerMetric<Package> {

    @Subscribe
    public void onClass(final MetricState state, final JavaClass c) {

    }

    @Subscribe
    public void onMethod(final MetricState state, final Method m) {

    }

    @Override
    public List<Package> getProduce(List<MetricState> states) {
        return Lists.newLinkedList();
    }

    @Override
    public Class<Package> getProducedClass() {
        return Package.class;
    }
}
