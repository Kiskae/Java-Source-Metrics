package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.bcel.ClassVisitor;
import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.bcel.classfile.JavaClass;

class JSMClassVisitorFactory implements ClassVisitorFactory {
    public final static JSMClassVisitorFactory INSTANCE = new JSMClassVisitorFactory();

    @Override
    public Runnable createClassVisitor(JavaClass targetClass, EventBus eventBus) {
        final ClassVisitor cVisitor = new ClassVisitor(targetClass, eventBus);
        return new Runnable() {
            @Override
            public void run() {
                cVisitor.start();
            }
        };
    }
}
