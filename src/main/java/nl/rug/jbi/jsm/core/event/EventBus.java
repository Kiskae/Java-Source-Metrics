package nl.rug.jbi.jsm.core.event;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.pipeline.HandlerExecutor;
import nl.rug.jbi.jsm.core.pipeline.HandlerMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author David van Leusen
 * @since 2014-05-28
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
                logger.trace("Ignoring {} because of invalidation", state);
                continue;
            }

            try {
                he.emitEvent(something, state);
            } catch (final MetricExecutionException e) {
                logger.warn(new ParameterizedMessage(
                                "Exception occurred whilst calculating '{}' for '{}', invalidating results.",
                                he.getMetricClass().getName(),
                                this.identifier
                ), e);

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

    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Destructive extraction of a subset of data, erases the state within this object and returns the references.
     *
     * @param keys Classes for which the data should be extracted.
     * @return
     */
    public Map<Class, MetricState> extractData(final List<Class> keys) {
        final Map<Class, MetricState> ret = Maps.newHashMap();

        for (final Class key : keys) {
            final MetricState state = this.stateMap.remove(key);
            if (state != null) {
                ret.put(key, state);
            } else {
                ret.put(key, new MetricState(this.identifier, key));
            }
        }

        return ret;
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
