package nl.rug.jbi.jsm.core.calculator;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: documentation
 */
public abstract class ProducerMetric extends BaseMetric {
    private final MetricScope produceScope;

    /**
     * @param dataScope
     * @param produceScope
     */
    public ProducerMetric(final MetricScope dataScope, final MetricScope produceScope) {
        super(dataScope);
        this.produceScope = checkNotNull(produceScope);
    }

    /**
     * @param states
     * @return
     */
    public abstract List<Produce> getProduce(final Map<String, MetricState> states);

    /**
     * @return
     */
    public abstract Class getProducedClass();

    /**
     * @return
     */
    public MetricScope getProduceScope() {
        return this.produceScope;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        //Since producers cannot produce results, they also don't create results for any scope.
        return EnumSet.noneOf(MetricScope.class);
    }

    /**
     * @param <P>
     */
    public class Produce<P> {
        private final String target;
        private final P produce;

        /**
         * @param target
         * @param produce
         */
        public Produce(final String target, final P produce) {
            this.target = checkNotNull(target);
            this.produce = checkNotNull(produce);
        }

        /**
         * @return
         */
        public MetricScope getScope() {
            return ProducerMetric.this.getProduceScope();
        }

        /**
         *
         * @return
         */
        public String getTarget() {
            return this.target;
        }

        /**
         *
         * @return
         */
        public P getProduce() {
            return this.produce;
        }
    }
}
