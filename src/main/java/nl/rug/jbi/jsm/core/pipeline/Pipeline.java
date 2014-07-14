package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.core.calculator.*;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The pipeline encapsulates the planned execution order of metrics, when data is available and what metrics to finalize
 * when. It does this by creating chains of {@link nl.rug.jbi.jsm.core.pipeline.PipelineFrame} for each scope, with each
 * frame building upon the data produced by the last frame.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class Pipeline {
    private final static Logger logger = LogManager.getLogger(Pipeline.class);

    private final Map<MetricScope, HandlerMap> handlerMaps = Maps.newEnumMap(MetricScope.class);
    private final Map<MetricScope, PipelineFrame> frameMap = Maps.newEnumMap(MetricScope.class);
    private final Map<Class<? extends ProducerMetric>, ProducerMetric> registeredProducers = Maps.newHashMap();

    /**
     * Creates a new pipeline-frame execution plan.
     *
     * @param defaultDataClasses The set of data available in the first frame of the class scope.
     */
    public Pipeline(final Set<Class> defaultDataClasses) {
        Preconditions.checkNotNull(defaultDataClasses, "The set of default data classes cannot be NULL.");
        Preconditions.checkArgument(Iterables.all(defaultDataClasses, new Predicate<Class>() {
            @Override
            public boolean apply(Class aClass) {
                return aClass != null;
            }
        }), "The set of default data classes cannot contain NULL.");

        this.handlerMaps.put(MetricScope.CLASS, new HandlerMap());
        this.handlerMaps.put(MetricScope.PACKAGE, new HandlerMap());
        this.handlerMaps.put(MetricScope.COLLECTION, new HandlerMap());

        this.frameMap.put(MetricScope.CLASS, new PipelineFrame(MetricScope.CLASS, defaultDataClasses));
        this.frameMap.put(MetricScope.PACKAGE, new PipelineFrame(MetricScope.PACKAGE));
        this.frameMap.put(MetricScope.COLLECTION, new PipelineFrame(MetricScope.COLLECTION));
    }

    /**
     * To be used in conjunction with {@link nl.rug.jbi.jsm.core.execution.PipelineExecutor#setClassVisitorFactory(nl.rug.jbi.jsm.core.execution.ClassVisitorFactory)}
     * to set up a custom ClassLoader with custom initial data. This method needs to be called any instances of this
     * class are created to prevent exceptions related to unknown data-types.
     *
     * @param dataType The data-type to register within the initial CLASS frame.
     * @deprecated Definition moved to class visitor factory, since the set is tied to it.
     */
    @Deprecated
    public static void registerNewBaseData(final Class dataType) {
        //NOOP
    }

    private Pair<Class, HandlerExecutor> processMethod(
            final Method listener,
            final BaseMetric metric,
            final Stack<Class> includeStack
    ) throws MetricPreparationException {
        final Class<?>[] params = listener.getParameterTypes();

        //Listener methods always have 2 parameters
        if (params.length != 2) {
            throw new MetricPreparationException(String.format(
                    "'%s' has more than 2 parameters.",
                    listener
            ));
        }

        //Method has to be set to public.
        if ((listener.getModifiers() & Modifier.PUBLIC) == 0) {
            throw new MetricPreparationException(String.format(
                    "'%s' isn't set to public",
                    listener
            ));
        }

        //The first parameter is always of type MetricState
        if (!MetricState.class.equals(params[0])) {
            throw new MetricPreparationException(String.format(
                    "The first parameter of '%s' isn't MetricState",
                    listener
            ));
        }

        if (listener.isAnnotationPresent(UsingProducer.class)) {
            final ProducerMetric producer = loadProducer(
                    listener.getAnnotation(UsingProducer.class).value(),
                    includeStack
            );

            //Ensure the producer produces for the current scope
            if (!producer.getProduceScope().equals(metric.getScope())) {
                throw new MetricPreparationException(String.format(
                        "'%s' operates on a different scope than '%s'",
                        producer.getClass().getName(),
                        listener.getClass().getName()
                ));
            }

            //Ensure the method expects the same type of data that the producer produces.
            if (!producer.getProducedClass().equals(params[1])) {
                throw new MetricPreparationException(String.format(
                        "Produce '%s' doesn't match the requested Class '%s' in '%s'",
                        producer.getProducedClass().getName(),
                        params[1].getName(),
                        listener
                ));
            }
        }

        return new Pair<Class, HandlerExecutor>(params[1], new HandlerExecutor(listener, metric));
    }

    private void registerMetricInternal(final BaseMetric metric, final Stack<Class> includeStack)
            throws MetricPreparationException {
        if (includeStack.contains(metric.getClass())) {
            throw new MetricPreparationException(String.format("Cyclic producer requirements: %s", includeStack));
        }
        includeStack.push(metric.getClass());

        final List<Pair<Class, HandlerExecutor>> executors = Lists.newLinkedList();

        for (final MetricScope resultScope : metric.getResultScopes()) {
            if (!metric.getScope().isValidNextScope(resultScope))
                throw new MetricPreparationException(
                        "A metric cannot produce results for scopes prior to its execution scope."
                );
        }

        for (final Method m : metric.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Subscribe.class)) {
                executors.add(processMethod(m, metric, includeStack));
            }
        }

        if (executors.isEmpty()) {
            logger.warn("{} has no defined data handlers, this might not be intended.", metric.getClass());
        }

        //Extract a set of data from the list of listeners.
        final Set<Class> usedData = FluentIterable.from(executors)
                .transform(new Function<Pair<Class, HandlerExecutor>, Class>() {
                    @Override
                    public Class apply(Pair<Class, HandlerExecutor> classHandlerExecutorPair) {
                        return classHandlerExecutorPair.first;
                    }
                })
                .toSet();

        PipelineFrame frame = checkNotNull(this.frameMap.get(metric.getScope()), "Undefined MetricScope");

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
                hMap.addHandler(pair.first, pair.second);
            }

            logger.debug("{} registered.", metric.getClass());
        } else {
            throw new MetricPreparationException(
                    "Unable to resolve data for metric, the required data might not have the same MetricScope"
            );
        }

        includeStack.pop();
    }

    /**
     * Registers a metric to be evaluated within this pipeline, it will be validated and then placed within a frame
     * where all the data required by the metric is available. Metrics registered need to subclass
     * {@link nl.rug.jbi.jsm.core.calculator.IsolatedMetric} or {@link nl.rug.jbi.jsm.core.calculator.SharedMetric}.ø
     *
     * @param metric Metric to registered to this pipeline.
     * @throws MetricPreparationException If there is an issue with the metric definition or its requirements, see the
     *                                    associated error for more information.
     */
    public void registerMetric(final BaseMetric metric) throws MetricPreparationException {
        if (!(metric instanceof IsolatedMetric || metric instanceof SharedMetric)) {
            throw new MetricPreparationException("A metric needs to be a subclass of IsolatedMetric or SharedMetric");
        } else {
            this.registerMetricInternal(metric, new Stack<Class>());
        }
    }

    /**
     * @return An unmodifiable map containing all scopes and the listeners for those scopes.
     */
    public Map<MetricScope, HandlerMap> getHandlerMaps() {
        return Collections.unmodifiableMap(this.handlerMaps);
    }

    /**
     * @return An unmodifiable map containing all scopes and the execution frames for those scopes.
     */
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

    private ProducerMetric loadProducer(
            final Class<? extends ProducerMetric> producerClass,
            final Stack<Class> includeStack
    ) throws MetricPreparationException {
        if (this.registeredProducers.containsKey(producerClass)) {
            return this.registeredProducers.get(producerClass);
        } else {
            logger.debug("Creating new producer: {}", producerClass);
            try {
                final ProducerMetric pm = producerClass.getConstructor().newInstance();
                this.registerMetricInternal(pm, includeStack);
                this.addProducer(pm);
                return pm;
            } catch (InvocationTargetException e) {
                throw new MetricPreparationException(
                        "Exception creating producer: " + producerClass.getName(),
                        e.getTargetException()
                );
            } catch (NoSuchMethodException e) {
                throw new MetricPreparationException("Producers require a zero-argument constructor.");
            } catch (InstantiationException e) {
                throw new MetricPreparationException("Exception in producer creation through reflection", e);
            } catch (IllegalAccessException e) {
                throw new MetricPreparationException("Exception in producer creation through reflection", e);
            }
        }
    }

    private void addProducer(final ProducerMetric pm) throws MetricPreparationException {
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
            if (!pm.getScope().isValidNextScope(pm.getProduceScope())) {
                throw new MetricPreparationException(String.format(
                        "Producer %s is trying to create produce in a scope that is executed before the producer can be run.",
                        pm.getClass().getName()
                ));
            }
        }

        frame.addDataClass(pm.getProducedClass());
        this.registeredProducers.put(pm.getClass(), pm);
    }

    /**
     * Get a list of metrics that produce results for the given scope, it does this by iterating over all registered
     * metrics and using {@link nl.rug.jbi.jsm.core.calculator.BaseMetric#getResultScopes()} to check if it produces for
     * the requested scope.
     *
     * @param scope The scope for which to find a list of metrics
     * @return A list of classes representing all metrics that produce results for the given scope.
     */
    public List<Class> getMetricsForScope(final MetricScope scope) {
        checkArgument(scope != null, "Scope cannot be NULL");

        return FluentIterable.from(this.frameMap.values())
                .transformAndConcat(new Function<PipelineFrame, Iterable<BaseMetric>>() {
                    @Override
                    public Iterable<BaseMetric> apply(PipelineFrame frame) {
                        final List<Iterable<? extends BaseMetric>> metrics = Lists.newLinkedList();

                        PipelineFrame currentFrame = frame;
                        while (currentFrame != null) {
                            metrics.add(currentFrame.getIsolatedMetrics());
                            metrics.add(currentFrame.getSharedMetrics());
                            currentFrame = currentFrame.getNextFrame();
                        }

                        return Iterables.concat(metrics);
                    }
                })
                .filter(new Predicate<BaseMetric>() {
                    @Override
                    public boolean apply(BaseMetric metric) {
                        return metric.getResultScopes().contains(scope);
                    }
                })
                .transform(new Function<BaseMetric, Class>() {
                    @Override
                    public Class apply(final BaseMetric metric) {
                        return metric.getClass();
                    }
                }).toList();
    }
}
