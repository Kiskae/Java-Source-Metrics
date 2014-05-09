package nl.rug.jbi.jsm.metrics;

import nl.rug.jbi.jsm.core.calculator.MetricState;

import java.util.List;

/**
 * @param <P>
 */
public interface ProducerMetric<P> {

    public List<P> getProduce(final List<MetricState> states);

    public Class<P> getProducedClass();
}
