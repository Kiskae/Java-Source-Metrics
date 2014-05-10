package nl.rug.jbi.jsm.core;

import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricState;

import java.util.Map;

public class EventBus {
    private final Map<Class, MetricState> stateList = Maps.newIdentityHashMap();

    public boolean hasListeners(final Class eventType) {
        //throw new UnsupportedOperationException("NYI");
        return true;
    }

    public void publish(final Object something) {
        //throw new UnsupportedOperationException("NYI");
        /*
        //TODO: get list of handlers for Object
        final List<Object> metricCalcs = Lists.newLinkedList();
        for (final Object calculator : metricCalcs) {
            //calculator.sendEvent(something, this.stateList.get(calculator.getClass()));
        }
        */
    }
}
