package nl.rug.jbi.jsm.core.calculator;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for a shared producer of data. This type of 'metric' is meant to be a way to reduce duplicate calculation
 * of data. Its evaluation operates very much like an {@link nl.rug.jbi.jsm.core.calculator.SharedMetric}, but it
 * produces a list of {@link nl.rug.jbi.jsm.core.calculator.ProducerMetric.Produce} instead of results.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public abstract class ProducerMetric extends BaseMetric {
    private final MetricScope produceScope;

    /**
     * @param dataScope    The operating scope of this metric.
     * @param produceScope Scope that the produce will belong to.
     */
    public ProducerMetric(final MetricScope dataScope, final MetricScope produceScope) {
        super(dataScope);
        this.produceScope = checkNotNull(produceScope);
    }

    /**
     * Evaluates some form of produce from the given states.
     *
     * @param states         Map of all unique sources and the states calculated from their data.
     * @param invalidMembers The number of sources for which calculation fails, it is up to the metric to decide
     *                       whether this means it cannot be accurately calculated or issue a warning.
     * @return A list of produce derived for the given map of states, should NEVER return NULL. If there is no produce
     * {@link com.google.common.collect.ImmutableList#of()} can be returned.
     */
    public abstract List<Produce> getProduce(final Map<String, MetricState> states, int invalidMembers);

    /**
     * @return Class of the data produced, used for association of data and producer with other metrics.
     */
    public abstract Class getProducedClass();

    /**
     * @return The scope where the produce needs to be delivered.
     */
    public MetricScope getProduceScope() {
        return this.produceScope;
    }

    @Override
    public final EnumSet<MetricScope> getResultScopes() {
        //Since producers cannot produce results, they also don't create results for any scope.
        return EnumSet.noneOf(MetricScope.class);
    }

    /**
     * Class which encapsulates an object that needs to be delivered at a later point. If the scope of delivery is
     * the same as the scope its calculated in, it will be delivered in the next
     * {@link nl.rug.jbi.jsm.core.pipeline.PipelineFrame}. If the scope of delivery is not the same as the one it is
     * calculated in, it will be stored for future delivery.
     */
    public class Produce {
        private final String target;
        private final Object produce;

        /**
         * @param target  Identifier of the target that the data belongs to.
         * @param produce Data to be delivered to metrics requesting it.
         * @throws java.lang.NullPointerException of either of the parameters is NULL.
         */
        public Produce(final String target, final Object produce) {
            this.target = checkNotNull(target);
            this.produce = checkNotNull(produce);
        }

        /**
         * @return Scope in which the produce needs to be delivered in.
         */
        public MetricScope getScope() {
            return ProducerMetric.this.getProduceScope();
        }

        /**
         * @return Identifier of the target that the produce belongs to.
         */
        public String getTarget() {
            return this.target;
        }

        /**
         * @return The produce that is to be delivered.
         */
        public Object getProduce() {
            return this.produce;
        }
    }
}
