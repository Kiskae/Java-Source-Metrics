package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Pipeline {
    private final static Logger logger = LogManager.getLogger(Pipeline.class);
    private final HandlerMap handlerMap = new HandlerMap();

    public void registerMetric(final BaseMetric metric) {
        //TODO TODO TODO TODO
        //Extract HandlerExecutors from class methods
        //If UsingProducer, ensure that producer is instantiated and registered
        //Bind data and processing to "Frames" in the pipeline.

        final List<Pair<Class, HandlerExecutor>> executors = Lists.newLinkedList();

        for (final Method m : metric.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Subscribe.class)) {
                if (m.isAnnotationPresent(UsingProducer.class)) {
                    logger.debug("{} required {}", metric.getClass().getSimpleName(), m.getAnnotation(UsingProducer.class).value().getSimpleName());
                }

                try {
                    final Class<?>[] params = m.getParameterTypes();
                    Preconditions.checkState(params.length == 2, "%s has more than 2 parameters", m);
                    Preconditions.checkState(
                            MetricState.class.equals(params[0]),
                            "%s's first parameter isn't MetricState",
                            m
                    );

                    executors.add(new Pair<Class, HandlerExecutor>(params[1], new HandlerExecutor(m, metric)));
                } catch (IllegalStateException e) {
                    logger.debug(e);
                }
            }
        }

        logger.debug(executors);
    }

    private static <A, B, C> Map<B, Map<A, C>> transposeMap(
            final Map<A, Map<B, C>> inputMap
    ) {
        final Map<B, Map<A, C>> ret = Maps.newHashMap();

        for (final Map.Entry<A, Map<B, C>> entry : inputMap.entrySet()) {
            final A ident = entry.getKey();
            for (final Map.Entry<B, C> entry2 : entry.getValue().entrySet()) {
                final Map<A, C> iMap;
                if (ret.containsKey(entry2.getKey())) {
                    iMap = ret.get(entry2.getKey());
                } else {
                    iMap = Maps.newHashMap();
                    ret.put(entry2.getKey(), iMap);
                }
                iMap.put(ident, entry2.getValue());
            }
        }

        return ret;
    }
}
