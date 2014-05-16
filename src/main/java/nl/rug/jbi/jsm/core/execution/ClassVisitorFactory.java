package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.bcel.classfile.JavaClass;

public interface ClassVisitorFactory {

    public Runnable createClassVisitor(final JavaClass targetClass, final EventBus eventBus);
}
