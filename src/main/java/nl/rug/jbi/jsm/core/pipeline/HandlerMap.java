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

    /*

    public static class Builder {
        private final List<Class> knownTypes = Lists.newLinkedList(BUILTIN_DATA_TYPES);
        private final List<IsolatedMetric> isolatedMetrics = Lists.newLinkedList();
        private final List<SharedMetric> sharedMetrics = Lists.newLinkedList();
        private final Map<Class, List<HandlerExecutor>> executors = Maps.newHashMap();

        public Builder addIsolatedMetric(final IsolatedMetric m) {
            this.isolatedMetrics.add(m);
            this.addExecutors(this.createExecutors(m));
            return this;
        }

        private void addExecutors(Map<Class, List<HandlerExecutor>> executors) {
            for (final Map.Entry<Class, List<HandlerExecutor>> entry : executors.entrySet()) {
                List<HandlerExecutor> handlers = this.executors.get(entry.getFirst());
                if (handlers == null) {
                    handlers = Lists.newLinkedList();
                    this.executors.put(entry.getFirst(), handlers);
                }
                handlers.addAll(entry.getSecond());
            }
        }

        private Map<Class, List<HandlerExecutor>> createExecutors(final Object metric) {
            final Method[] methods = metric.getClass().getDeclaredMethods();
            final Map<Class, List<HandlerExecutor>> executors = Maps.newHashMap();

            for (final Method method : methods) {
                if (method.isAnnotationPresent(UsingProducer.class)) {
                    final UsingProducer annotation = method.getAnnotation(UsingProducer.class);
                    //TODO: resolve annotation.value()
                }

                if (method.isAnnotationPresent(Subscribe.class)) {
                    final Class<?>[] param = method.getParameterTypes();
                    Preconditions.checkState(param.length == 2);
                    Preconditions.checkState(MetricState.class.equals(param[0]));
                    Preconditions.checkState(this.knownTypes.contains(param[1]), param[1]);
                    List<HandlerExecutor> handlers = executors.get(param[1]);
                    if (handlers == null) {
                        handlers = Lists.newLinkedList();
                        executors.put(param[1], handlers);
                    }
                    handlers.add(new HandlerExecutor(method, metric));
                }
            }
            return executors;
        }

        public Builder addSharedMetric(final SharedMetric m) {
            this.sharedMetrics.add(m);
            this.addExecutors(this.createExecutors(m));
            return this;
        }

        public HandlerMap build() {
            return new HandlerMap(this.executors);
        }
    }
    */
}
