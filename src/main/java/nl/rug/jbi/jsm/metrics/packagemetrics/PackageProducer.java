package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.bcel.MethodDefinition;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Map;

public class PackageProducer extends ProducerMetric {

    public PackageProducer() {
        //    Data Scope         Produce Scope
        super(MetricScope.CLASS, MetricScope.PACKAGE);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition c) {
        state.setValue("extends-class", c.getSuperClass());
    }

    @Subscribe
    public void onMethod(final MetricState state, final MethodDefinition m) {

    }

    @Override
    public List<Produce> getProduce(Map<String, MetricState> states) {
        return ImmutableList.of();
    }

    @Override
    public Class getProducedClass() {
        return Package.class;
    }
}
