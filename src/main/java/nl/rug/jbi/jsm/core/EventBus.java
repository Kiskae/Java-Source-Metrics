package nl.rug.jbi.jsm.core;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.HandlerExecutor;
import nl.rug.jbi.jsm.core.event.HandlerMap;

import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class, MetricState> stateList = Maps.newIdentityHashMap();
    private final HandlerMap handlers;
    private final String identifier;

    public EventBus(final String identifier, final HandlerMap handlers) {
        this.identifier = Preconditions.checkNotNull(identifier);
        this.handlers = Preconditions.checkNotNull(handlers);
    }

    public boolean hasListeners(final Class eventType) {
        return !this.handlers.getHandlers(eventType).isEmpty();
    }

    public void publish(final Object something) {
        Preconditions.checkArgument(something != null);
        assert something != null;
        final List<HandlerExecutor> executorList = this.handlers.getHandlers(something.getClass());

        for (final HandlerExecutor he : executorList) {
            he.sendEvent(something, this.getState(he.getMetricClass()));
        }

        //throw new UnsupportedOperationException("NYI");
        /*
        //TODO: get list of handlers for Object
        final List<Object> metricCalcs = Lists.newLinkedList();
        for (final Object calculator : metricCalcs) {
            //calculator.sendEvent(something, this.stateList.get(calculator.getClass()));
        }
        */
    }

    private MetricState getState(final Class metricClass) {
        MetricState state = this.stateList.get(metricClass);
        if (state == null) {
            state = new MetricState(this.identifier, metricClass);
            this.stateList.put(metricClass, state);
        }
        return state;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("stateList", stateList)
                .add("handlers", handlers)
                .add("identifier", identifier)
                .toString();
    }
}
