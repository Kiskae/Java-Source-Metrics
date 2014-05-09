package nl.rug.jbi.jsm.core;

import nl.rug.jbi.jsm.core.calculator.Subscribe;
import nl.rug.jbi.jsm.core.calculator.UsingProducer;
import nl.rug.jbi.jsm.metrics.IsolatedMetric;
import nl.rug.jbi.jsm.metrics.SharedMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class JSMCore {
    private final static Logger logger = LogManager.getLogger(JSMCore.class);

    public void registerMetric(final IsolatedMetric m) {
        this.resolveListeners(m.getClass());
    }

    public void registerMetric(final SharedMetric m) {
        this.resolveListeners(m.getClass());
    }

    public void resolveListeners(final Class c) {
        final Method[] methods = c.getDeclaredMethods();
        for (final Method method : methods) {
            if (method.isAnnotationPresent(UsingProducer.class)) {
                final UsingProducer annotation = method.getAnnotation(UsingProducer.class);
                this.resolveListeners(annotation.value());
            }

            if (method.isAnnotationPresent(Subscribe.class)) {
                logger.debug("{} is a listener!", method);
            }
        }
    }
}
