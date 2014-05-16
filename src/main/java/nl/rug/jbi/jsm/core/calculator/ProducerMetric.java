package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

public abstract class ProducerMetric extends BaseMetric {
    private final MetricScope produceScope;

    public ProducerMetric(final MetricScope dataScope, final MetricScope produceScope) {
        super(dataScope);
        this.produceScope = Preconditions.checkNotNull(produceScope);
    }

    public abstract List<Produce> getProduce(final Map<String, MetricState> states);

    public abstract Class getProducedClass();

    public MetricScope getProduceScope() {
        return this.produceScope;
    }

    public class Produce<P> {
        private final String target;
        private final P produce;

        public Produce(final String target, final P produce) {
            this.target = Preconditions.checkNotNull(target);
            this.produce = Preconditions.checkNotNull(produce);
        }

        public MetricScope getScope() {
            return ProducerMetric.this.getProduceScope();
        }

        public String getTarget() {
            return this.target;
        }

        public P getProduce() {
            return this.produce;
        }
    }
}
