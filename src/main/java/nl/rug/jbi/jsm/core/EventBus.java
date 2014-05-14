package nl.rug.jbi.jsm.core;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.HandlerExecutor;
import nl.rug.jbi.jsm.core.event.HandlerMap;
import nl.rug.jbi.jsm.core.event.MetricExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author David van Leusen
 * @since 1.0
 */
public class EventBus {
    private final static Logger logger = LogManager.getLogger(EventBus.class);
    private final Map<Class, MetricState> stateMap = Maps.newIdentityHashMap();
    private final HandlerMap handlerMap;
    private final String identifier;

    public EventBus(final String identifier, final HandlerMap handlerMap) {
        this.identifier = Preconditions.checkNotNull(identifier);
        this.handlerMap = Preconditions.checkNotNull(handlerMap);
    }

    /**
     * @param eventType
     * @return
     */
    public boolean hasListeners(final Class eventType) {
        return this.handlerMap.getHandlers(eventType).size() != 0;
    }

    /**
     * @param something
     */
    public void publish(final Object something) {
        Preconditions.checkArgument(something != null);
        assert something != null;
        final List<HandlerExecutor> executorList = this.handlerMap.getHandlers(something.getClass());

        for (final HandlerExecutor he : executorList) {
            final MetricState state = this.getState(he.getMetricClass());

            if (!state.isValid()) {
                logger.debug("Ignoring {} because of invalidation", state);
                continue;
            }

            try {
                he.emitEvent(something, state);
            } catch (final MetricExecutionException e) {
                state.invalidate(e);
            }
        }
    }

    /**
     * @return An {@link java.util.Collections.UnmodifiableMap} view of the internal state map
     */
    public Map<Class, MetricState> getStates() {
        return Collections.unmodifiableMap(this.stateMap);
    }

    private MetricState getState(final Class metricClass) {
        MetricState state = this.stateMap.get(metricClass);
        if (state == null) {
            state = new MetricState(this.identifier, metricClass);
            this.stateMap.put(metricClass, state);
        }
        return state;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("stateMap", stateMap)
                .add("handlerMap", handlerMap)
                .add("identifier", identifier)
                .toString();
    }
}
