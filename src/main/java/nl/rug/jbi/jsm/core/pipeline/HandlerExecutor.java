package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.MetricExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class encapsulates a object-method pair accepting a certain data-type, derived from a metric.
 * As data becomes available it gets sent through this object together with the associated MetricState to mutate that
 * state using the method.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class HandlerExecutor {
    private final Method eventMethod;
    private final BaseMetric metricCalculator;

    HandlerExecutor(final Method eventMethod, final BaseMetric metricCalculator) {
        this.eventMethod = checkNotNull(eventMethod);
        this.metricCalculator = checkNotNull(metricCalculator);
    }

    /**
     * @return The metric class this handler is built from.
     */
    public Class getMetricClass() {
        return this.metricCalculator.getClass();
    }

    /**
     * @return The scope in which this handler gets executed.
     */
    public MetricScope getScope() {
        return this.metricCalculator.getScope();
    }

    /**
     * Applies the internal method to the given data and state, mutating the state to progress to the result.
     *
     * @param obj   Data to publish
     * @param state External state associated with the data-source.
     * @throws MetricExecutionException If an exception occurs whilst executing the method.
     */
    public void emitEvent(final Object obj, final MetricState state) throws MetricExecutionException {
        try {
            this.eventMethod.invoke(this.metricCalculator, state, obj);
        } catch (IllegalAccessException e) {
            throw new MetricExecutionException("Event handler method set to private", e);
        } catch (InvocationTargetException e) {
            throw new MetricExecutionException("Exception executing metric calculator", e.getCause());
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("eventMethod", eventMethod)
                .add("object", metricCalculator)
                .toString();
    }
}
