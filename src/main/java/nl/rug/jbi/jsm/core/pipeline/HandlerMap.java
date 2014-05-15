package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class HandlerMap {
    private final static List<HandlerExecutor> EMPTY_LIST = ImmutableList.of();
    private final Map<Class, List<HandlerExecutor>> handlers = Maps.newIdentityHashMap();

    public List<HandlerExecutor> getHandlers(final Class dataType) {
        return this.handlers.getOrDefault(dataType, EMPTY_LIST);
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
