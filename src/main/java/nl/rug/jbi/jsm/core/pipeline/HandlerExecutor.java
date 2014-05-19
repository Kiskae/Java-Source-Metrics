package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.MetricExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerExecutor {
    private final Method eventMethod;
    private final BaseMetric metricCalculator;

    public HandlerExecutor(final Method eventMethod, final BaseMetric metricCalculator) {
        this.eventMethod = Preconditions.checkNotNull(eventMethod);
        this.metricCalculator = Preconditions.checkNotNull(metricCalculator);
    }

    public Class getMetricClass() {
        return this.metricCalculator.getClass();
    }

    public MetricScope getScope() {
        return this.metricCalculator.getScope();
    }

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
