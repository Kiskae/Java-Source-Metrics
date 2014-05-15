package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.bcel.JavaClassData;
import nl.rug.jbi.jsm.bcel.MethodData;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Map;

public class PackageProducer extends ProducerMetric<Package> {

    public PackageProducer() {
        //    Data Scope         Produce Scope
        super(MetricScope.CLASS, MetricScope.PACKAGE);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassData c) {
        state.setValue("extends-class", c.getSuperClass());
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodData m) {

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
