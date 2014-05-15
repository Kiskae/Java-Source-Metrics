package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

/**
 * @param <P>
 */
public abstract class ProducerMetric<P> extends BaseMetric {
    private final MetricScope produceScope;

    public ProducerMetric(final MetricScope dataScope, final MetricScope produceScope) {
        super(dataScope);
        this.produceScope = Preconditions.checkNotNull(produceScope);
    }

    public abstract List<Produce<P>> getProduce(final Map<String, MetricState> states);

    public abstract Class<P> getProducedClass();

    public MetricScope getProduceScope() {
        return this.produceScope;
    }

    public static class Produce<P> {
        private final String target;
        private final P produce;

        public Produce(final String target, final P produce) {
            this.target = Preconditions.checkNotNull(target);
            this.produce = Preconditions.checkNotNull(produce);
        }

        public String getTarget() {
            return this.target;
        }

        public P getProduce() {
            return this.produce;
        }
    }
}
