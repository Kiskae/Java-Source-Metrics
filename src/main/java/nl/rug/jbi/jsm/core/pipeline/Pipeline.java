package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.bcel.FieldData;
import nl.rug.jbi.jsm.bcel.JavaClassData;
import nl.rug.jbi.jsm.bcel.MethodData;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pipeline {
    private final static List<Class> BASE_DATA_CLASSES = Lists.<Class>newArrayList(
            JavaClassData.class,
            MethodData.class,
            FieldData.class
    );
    private final static Logger logger = LogManager.getLogger(Pipeline.class);

    private final Map<MetricScope, HandlerMap> handlerMaps = Maps.newEnumMap(MetricScope.class);
    private final Map<MetricScope, PipelineFrame> frameMap = Maps.newEnumMap(MetricScope.class);
    private final Map<Class<? extends ProducerMetric>, ProducerMetric> registeredProducers = Maps.newHashMap();

    public Pipeline() {
        this.handlerMaps.put(MetricScope.CLASS, new HandlerMap());
        this.handlerMaps.put(MetricScope.PACKAGE, new HandlerMap());
        this.handlerMaps.put(MetricScope.COLLECTION, new HandlerMap());

        this.frameMap.put(MetricScope.CLASS, new PipelineFrame(MetricScope.CLASS, BASE_DATA_CLASSES));
        this.frameMap.put(MetricScope.PACKAGE, new PipelineFrame(MetricScope.PACKAGE));
        this.frameMap.put(MetricScope.COLLECTION, new PipelineFrame(MetricScope.COLLECTION));
    }

    public void registerMetric(final BaseMetric metric) throws MetricPreparationException {
        final List<Pair<Class, HandlerExecutor>> executors = Lists.newLinkedList();

        for (final Method m : metric.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Subscribe.class)) {
                try {
                    final Class<?>[] params = m.getParameterTypes();

                    //Listener methods always have 2 parameters
                    Preconditions.checkState(params.length == 2, "'%s' has more than 2 parameters", m);

                    //The first parameter is always of type MetricState
                    Preconditions.checkState(
                            MetricState.class.equals(params[0]),
                            "The first parameter of '%s' isn't MetricState",
                            m
                    );

                    if (m.isAnnotationPresent(UsingProducer.class)) {
                        final ProducerMetric producer = loadProducer(m.getAnnotation(UsingProducer.class).value());

                        //Ensure the producer produces for the current scope
                        Preconditions.checkState(
                                producer.getProduceScope().equals(metric.getScope()),
                                "'%s' operates on a different scope than '%s'",
                                producer.getClass().getName(),
                                metric.getClass().getName()
                        );

                        //Ensure the Producer produces the right type of data
                        Preconditions.checkState(
                                producer.getProducedClass().equals(params[1]),
                                "Produce '%s' doesn't match the requested Class '%s' in '%s'",
                                producer.getProducedClass().getName(),
                                params[1].getName(),
                                m
                        );
                    }

                    executors.add(new Pair<Class, HandlerExecutor>(params[1], new HandlerExecutor(m, metric)));
                } catch (IllegalStateException e) {
                    throw new MetricPreparationException("Exception in method definition: " + m, e);
                }
            }
        }

        final Set<Class> usedData = Sets.newHashSet(
                Lists.transform(executors, new Function<Pair<Class, HandlerExecutor>, Class>() {
                    @Override
                    public Class apply(Pair<Class, HandlerExecutor> classHandlerExecutorPair) {
                        return classHandlerExecutorPair.getFirst();
                    }
                })
        );

        PipelineFrame frame = Preconditions.checkNotNull(this.frameMap.get(metric.getScope()), "Undefined MetricScope");

        while (frame != null) {
            if (frame.checkAvailableData(usedData)) {
                break;
            }
            frame = frame.getNextFrame();
        }

        if (frame != null) {
            frame.registerMetric(metric);

            //Add handlers to the HandlerMap
            final HandlerMap hMap = this.handlerMaps.get(metric.getScope());
            for (final Pair<Class, HandlerExecutor> pair : executors) {
                hMap.addHandler(pair.getFirst(), pair.getSecond());
            }

            logger.debug("{} registered.", metric.getClass());
        } else {
            throw new MetricPreparationException(
                    "Unable to resolve data for metric, the required data might not have a single MetricScope",
                    null
            );
        }
    }

    public Map<MetricScope, HandlerMap> getHandlerMaps() {
        return Collections.unmodifiableMap(this.handlerMaps);
    }

    public Map<MetricScope, PipelineFrame> getPipelineFrames() {
        return Collections.unmodifiableMap(this.frameMap);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("handlerMaps", handlerMaps)
                .add("frameMap", frameMap)
                .toString();
    }

    private ProducerMetric loadProducer(final Class<? extends ProducerMetric> producerClass) throws MetricPreparationException {
        if (this.registeredProducers.containsKey(producerClass)) {
            return this.registeredProducers.get(producerClass);
        } else {
            logger.debug("Creating new producer: {}", producerClass);
            try {
                final ProducerMetric pm = producerClass.getConstructor().newInstance();
                this.registerMetric(pm);
                this.addProducer(pm);
                return pm;
            } catch (InvocationTargetException e) {
                throw new MetricPreparationException("Exception creating producer: " + producerClass.getName(), e.getTargetException());
            } catch (NoSuchMethodException e) {
                throw new MetricPreparationException("Producers require a zero-argument constructor.", null);
            } catch (ReflectiveOperationException e) {
                throw new MetricPreparationException("Exception in producer creation through reflection", e);
            }
        }
    }

    private void addProducer(final ProducerMetric pm) {
        PipelineFrame frame = this.frameMap.get(pm.getProduceScope());
        if (pm.getScope() == pm.getProduceScope()) {
            //Scope X -> X, so data available in the frame after the metric has been calculated.
            for (; ; ) {
                if (frame.getProducerMetrics().contains(pm))
                    break;
                frame = frame.getNextFrame();
            }

            if (frame.getNextFrame() != null) {
                frame = frame.getNextFrame();
            } else {
                frame = new PipelineFrame(frame);
            }
        } else {
            //Scope X -> Y, data available in first frame of Y
        }

        frame.addDataClass(pm.getProducedClass());
        this.registeredProducers.put(pm.getClass(), pm);
    }

    public List<Class> getMetricsForScope(final MetricScope scope) {
        final List<Class> ret = Lists.newLinkedList();
        final Function<BaseMetric, Class> extractor = new Function<BaseMetric, Class>() {
            @Override
            public Class apply(final BaseMetric metric) {
                return metric.getClass();
            }
        };

        PipelineFrame currentFrame = this.frameMap.get(Preconditions.checkNotNull(scope));
        while (currentFrame != null) {
            ret.addAll(Lists.transform(currentFrame.getIsolatedMetrics(), extractor));
            ret.addAll(Lists.transform(currentFrame.getSharedMetrics(), extractor));
            currentFrame = currentFrame.getNextFrame();
        }

        return ret;
    }
}
