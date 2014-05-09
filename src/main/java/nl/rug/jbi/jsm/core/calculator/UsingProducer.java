package nl.rug.jbi.jsm.core.calculator;

import nl.rug.jbi.jsm.metrics.ProducerMetric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingProducer {

    public Class<? extends ProducerMetric> value();
}
