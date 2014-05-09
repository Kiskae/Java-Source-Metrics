package nl.rug.jbi.jsm.metrics.packagemetrics;

import nl.rug.jbi.jsm.bcel.Method;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.Subscribe;
import nl.rug.jbi.jsm.metrics.ProducerMetric;

import java.util.List;

public class PackageProducer implements ProducerMetric<Object> {

    @Subscribe
    public void onMethod(final MetricState state, final Method m) {

    }

    @Override
    public List<Object> getProduce(List<MetricState> states) {
        return null;
    }

    @Override
    public Class<Object> getProducedClass() {
        return Object.class;
    }
}
