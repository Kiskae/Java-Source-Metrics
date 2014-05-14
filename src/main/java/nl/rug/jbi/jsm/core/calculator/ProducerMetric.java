package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

/**
 * @param <P>
 */
public interface ProducerMetric<P> {

    public List<Produce<P>> getProduce(final Map<String, MetricState> states);

    public Class<P> getProducedClass();

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
