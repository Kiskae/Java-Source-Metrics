package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.bcel.classfile.JavaClass;

import java.util.Set;

/**
 * Factory for the ClassVisitor runnable, given the {@link org.apache.bcel.classfile.JavaClass} and the associated
 * EventBus, a class visitor needs to be created which extracts data from the JavaClass and publishes it through the
 * EventBus.
 * The default ClassVisitor is {@link nl.rug.jbi.jsm.bcel.ClassVisitor}.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public interface ClassVisitorFactory {

    /**
     * Create a class visitor for the given class and event bus, the produced object should be a Runnable and  this
     * visitor should be start when {@link Runnable#run()} is called.
     *
     * @param targetClass Class to evaluate
     * @param eventBus    EventBus associated with the given class.
     * @return Runnable that will execute the class visitor.
     */
    public Runnable createClassVisitor(final JavaClass targetClass, final EventBus eventBus);

    /**
     * If the {@link nl.rug.jbi.jsm.bcel.ClassVisitor} is extended, this should return a super-set of
     * {@link nl.rug.jbi.jsm.bcel.ClassVisitor#DEFAULT_CLASSES}.
     *
     * @return The set of classes which get produced by the class visitors this factory creates.
     */
    public Set<Class> getDefaultDataClasses();
}
