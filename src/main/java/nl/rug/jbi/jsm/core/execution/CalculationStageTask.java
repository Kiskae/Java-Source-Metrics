package nl.rug.jbi.jsm.core.execution;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Runnable that performs the first stage of the frame execution pipeline.
 * This consists of the following steps:
 * <ul>
 * <li>Execute the provided modifier, this is a Class Visitor or a {@link nl.rug.jbi.jsm.core.execution.DataListDispatcher}</li>
 * <li>Calculate the {@link nl.rug.jbi.jsm.core.calculator.IsolatedMetric} for the associated class.</li>
 * <li>Send the results, if they exist, to the frontend.</li>
 * </ul>
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
class CalculationStageTask implements Runnable {
    private final static Logger logger = LogManager.getLogger(CalculationStageTask.class);
    private final CountDownLatch latch;
    private final Runnable modifier;
    private final EventBus dataStore;
    private final List<IsolatedMetric> metricList;
    private final Predicate<List<MetricResult>> resultCallback;

    CalculationStageTask(
            final CountDownLatch latch,
            final Runnable modifier,
            final EventBus dataStore,
            final List<IsolatedMetric> metricList,
            final Predicate<List<MetricResult>> resultCallback
    ) {
        this.latch = latch;
        this.modifier = modifier;
        this.dataStore = dataStore;
        this.metricList = metricList;
        this.resultCallback = resultCallback;
    }

    @Override
    public void run() {
        try {
            //Run the modifier
            this.modifier.run();

            //Extract the data for the Isolated metrics.
            final Map<Class, MetricState> isolatedMetricData = this.dataStore.extractData(
                    Lists.transform(this.metricList, new Function<IsolatedMetric, Class>() {
                        @Override
                        public Class apply(final IsolatedMetric isolatedMetric) {
                            return isolatedMetric.getClass();
                        }
                    })
            );

            //Calculate results
            final List<MetricResult> results = Lists.newLinkedList();
            for (final IsolatedMetric m : this.metricList) {
                final MetricState state = isolatedMetricData.get(m.getClass());
                if (state.isValid()) {
                    try {
                        results.add(m.getResult(this.dataStore.getIdentifier(), state));
                        continue;
                    } catch (final Exception ex) {
                        logger.warn(
                                new ParameterizedMessage(
                                        "Exception occurred while finalizing '{}' for '{}'",
                                        m.getClass().getName(),
                                        state.getIdentifier()
                                ),
                                ex
                        );
                    }
                } else {
                    logger.warn("Failed to calculate '{}' for '{}'", m.getClass().getName(), state.getIdentifier());
                }

                results.add(new InvalidResult(this.dataStore.getIdentifier(), m, "Error during calculation"));
            }

            //Deliver Results
            if (!results.isEmpty())
                this.resultCallback.apply(results);

            //It will now return, at which point shared/producer metrics will be calculated and the pipeline
            //will advance.
        } finally {
            latch.countDown();
        }
    }
}
