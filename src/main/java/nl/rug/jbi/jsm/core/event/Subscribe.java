package nl.rug.jbi.jsm.core.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method within a {@link nl.rug.jbi.jsm.core.calculator.IsolatedMetric},
 * {@link nl.rug.jbi.jsm.core.calculator.SharedMetric} or {@link nl.rug.jbi.jsm.core.calculator.ProducerMetric} as a
 * method that receives data from {@link nl.rug.jbi.jsm.core.event.EventBus}.
 * The requirement of methods marked by this annotation is that the method is marked as public, the first parameter is
 * of the type {@link nl.rug.jbi.jsm.core.calculator.MetricState} and the second type is a data-type defined by one of
 * the following reasons:
 * <ul>
 * <li>One of the data-types defined in {@link nl.rug.jbi.jsm.bcel}</li>
 * <li>An extended data-type registered with {@link nl.rug.jbi.jsm.core.pipeline.Pipeline#registerNewBaseData(Class)}</li>
 * <li>A data-type produced by a producer, requires the additional {@link nl.rug.jbi.jsm.core.event.UsingProducer}
 * annotation.</li>
 * </ul>
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
}
