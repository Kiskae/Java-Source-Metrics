package nl.rug.jbi.jsm.core.event;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.calculator.MetricState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerExecutor {
    private final Method eventMethod;
    private final Object metricCalculator;

    public HandlerExecutor(final Method eventMethod, final Object metricCalculator) {
        this.eventMethod = Preconditions.checkNotNull(eventMethod);
        this.metricCalculator = Preconditions.checkNotNull(metricCalculator);
    }

    public Class getMetricClass() {
        return this.metricCalculator.getClass();
    }

    public void sendEvent(final Object obj, final MetricState state) {
        //TODO: event handling for metric calculators...
        try {
            eventMethod.invoke(this.metricCalculator, state, obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
