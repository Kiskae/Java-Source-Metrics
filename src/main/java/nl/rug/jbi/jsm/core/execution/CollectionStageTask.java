package nl.rug.jbi.jsm.core.execution;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.util.BiFunction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

class CollectionStageTask<M, R> implements Callable<List<R>> {
    private final static Predicate<MetricState> VALIDITY_FILTER = new Predicate<MetricState>() {
        @Override
        public boolean apply(final MetricState state) {
            return state.isValid();
        }
    };

    private final Map<String, MetricState> data;
    private final M metric;
    private final BiFunction<M, Map<String, MetricState>, List<R>> converter;
    private final CountDownLatch latch;

    private CollectionStageTask(
            final Map<String, MetricState> data,
            final M metric,
            final BiFunction<M, Map<String, MetricState>, List<R>> converter,
            final CountDownLatch latch
    ) {

        this.data = data;
        this.metric = metric;
        this.converter = converter;
        this.latch = latch;
    }

    public static CollectionStageTask<ProducerMetric, ProducerMetric.Produce> forProducer(
            final ProducerMetric producer,
            final Map<String, MetricState> data,
            final CountDownLatch latch
    ) {
        return new CollectionStageTask<ProducerMetric, ProducerMetric.Produce>(
                data,
                producer,
                new BiFunction<ProducerMetric, Map<String, MetricState>, List<ProducerMetric.Produce>>() {
                    @Override
                    public List<ProducerMetric.Produce> apply(ProducerMetric calc, Map<String, MetricState> data) {
                        final Map<String, MetricState> filteredData = Maps.filterValues(data, VALIDITY_FILTER);
                        return calc.getProduce(filteredData, data.size() - filteredData.size());
                    }
                },
                latch
        );
    }

    public static CollectionStageTask<SharedMetric, MetricResult> forSharedMetric(
            final SharedMetric metric,
            final Map<String, MetricState> data,
            final CountDownLatch latch
    ) {
        return new CollectionStageTask<SharedMetric, MetricResult>(
                data,
                metric,
                new BiFunction<SharedMetric, Map<String, MetricState>, List<MetricResult>>() {
                    @Override
                    public List<MetricResult> apply(SharedMetric calc, Map<String, MetricState> data) {
                        final Map<String, MetricState> filteredData = Maps.filterValues(data, VALIDITY_FILTER);
                        return calc.getResults(filteredData, data.size() - filteredData.size());
                    }
                },
                latch
        );
    }

    @Override
    public List<R> call() throws Exception {
        try {
            return this.converter.apply(this.metric, this.data);
        } finally {
            latch.countDown();
        }
    }
}
