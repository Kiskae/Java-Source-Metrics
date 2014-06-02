package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Container for a set of handlers, associating data-types to a list of {@link nl.rug.jbi.jsm.core.pipeline.HandlerExecutor}.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class HandlerMap {
    private final static List<HandlerExecutor> EMPTY_LIST = ImmutableList.of();
    private final Map<Class, List<HandlerExecutor>> handlers = Maps.newIdentityHashMap();

    /**
     * Get the list of handlers for the given data-type.
     * Will always return a list even if there are no handlers for the given data-type.
     *
     * @param dataType data-type to retrieve the handlers for.
     * @return List of handlers for the given data-type, never returns NULL.
     */
    public List<HandlerExecutor> getHandlers(final Class dataType) {
        final List<HandlerExecutor> ret = this.handlers.get(dataType);
        return ret != null ? ret : EMPTY_LIST;
    }

    void addHandler(final Class identifierClass, final HandlerExecutor executor) {
        if (this.handlers.containsKey(identifierClass)) {
            this.handlers.get(identifierClass).add(executor);
        } else {
            this.handlers.put(identifierClass, Lists.newLinkedList(ImmutableList.of(executor)));
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("handlers", handlers)
                .toString();
    }
}
