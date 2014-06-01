package nl.rug.jbi.jsm.core.event;

import nl.rug.jbi.jsm.core.calculator.ProducerMetric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that acts as an extension to {@link nl.rug.jbi.jsm.core.event.Subscribe}. It provides a
 * {@link nl.rug.jbi.jsm.core.calculator.ProducerMetric} for a custom data-type.
 * Adding this annotation means the producer will be registered if it isn't already; And the produce will get delivered
 * to the metric marked afterwards.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingProducer {

    /**
     * @return The producer that needs to be loaded to provide the data-value required by the marked method.
     */
    public Class<? extends ProducerMetric> value();
}
