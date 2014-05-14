package nl.rug.jbi.jsm.core.event;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.bcel.JavaClass;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.IsolatedMetric;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class HandlerMap {
    private static final List<HandlerExecutor> EMPTY_LIST = ImmutableList.of();
    private static final List<Class> BUILTIN_DATA_TYPES = Lists.<Class>newArrayList(
            JavaClass.class,
            nl.rug.jbi.jsm.bcel.Method.class
    );

    private final Map<Class, List<HandlerExecutor>> handlerMap;

    private HandlerMap(final Map<Class, List<HandlerExecutor>> handlers) {
        Preconditions.checkArgument(handlers != null);

        final ImmutableMap.Builder<Class, List<HandlerExecutor>> builder = ImmutableMap.builder();
        assert handlers != null;
        for (Map.Entry<Class, List<HandlerExecutor>> entry : handlers.entrySet()) {
            builder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
        }

        this.handlerMap = builder.build();
    }

    public List<HandlerExecutor> getHandlers(final Class dataType) {
        return this.handlerMap.getOrDefault(dataType, EMPTY_LIST);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("handlerMap", handlerMap)
                .toString();
    }

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
                List<HandlerExecutor> handlers = this.executors.get(entry.getKey());
                if (handlers == null) {
                    handlers = Lists.newLinkedList();
                    this.executors.put(entry.getKey(), handlers);
                }
                handlers.addAll(entry.getValue());
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
}
