package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default implementation of the Visitor pattern for {@link org.apache.bcel.classfile.JavaClass}. This class serves to
 * take the BCEL data and emit structural data based on this data. It is possible to override this class-visitor with a
 * custom implementation if additional data is required. See the associated classes/methods for more information.
 *
 * @author David van Leusen
 * @see nl.rug.jbi.jsm.core.execution.ClassVisitorFactory
 * @see nl.rug.jbi.jsm.core.execution.PipelineExecutor#setClassVisitorFactory(nl.rug.jbi.jsm.core.execution.ClassVisitorFactory)
 * @see nl.rug.jbi.jsm.core.pipeline.Pipeline#registerNewBaseData(Class)
 * @since 2014-05-28
 */
public class ClassVisitor implements Visitor {
    public final static ImmutableSet<Class> DEFAULT_CLASSES = ImmutableSet.<Class>builder()
            .add(JavaClassDefinition.class)
            .add(MethodDefinition.class)
            .add(FieldDefinition.class)
            .add(ExceptionHandlerDefinition.class)
            .add(FieldAccessInstr.class)
            .add(InvokeMethodInstr.class)
            .add(TypeUseInstruction.class)
            .add(LocalVariableDefinition.class)
            .build();
    private final static Logger logger = LogManager.getLogger(ClassVisitor.class);
    private final JavaClass visitedClass;
    private final EventBus eBus;
    private final ConstantPoolGen cp;

    public ClassVisitor(final JavaClass jc, final EventBus eBus) {
        this.visitedClass = jc;
        this.eBus = eBus;
        this.cp = new ConstantPoolGen(this.visitedClass.getConstantPool());
    }

    /**
     * Exposes EventBus to overriding implementations.
     *
     * @return EventBus assigned to this visitor.
     */
    protected EventBus getEventBus() {
        return this.eBus;
    }

    /**
     * Override this method to substitute a custom MethodVisitor implementation.
     *
     * @param mg Method data
     * @return A visitor that is ready to execute.
     */
    protected MethodVisitor createMethodVisitor(final MethodGen mg) {
        return new MethodVisitor(mg, this.getEventBus());
    }

    public void start() {
        this.visitJavaClass(this.visitedClass);
    }

    @Override
    public void visitCode(Code obj) {
        logger.trace(obj);
    }

    @Override
    public void visitCodeException(CodeException obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantLong(ConstantLong obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantPool(ConstantPool obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantString(ConstantString obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantValue(ConstantValue obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDeprecated(Deprecated obj) {
        logger.trace(obj);
    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {
        logger.trace(obj);
    }

    @Override
    public void visitField(Field obj) {
        logger.trace(obj);

        if (this.getEventBus().hasListeners(FieldDefinition.class)) {
            this.getEventBus().publish(new FieldDefinition(obj));
        }
    }

    @Override
    public void visitInnerClass(InnerClass obj) {
        logger.trace(obj);
    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {
        logger.trace(obj);
    }

    @Override
    public void visitJavaClass(JavaClass jc) {
        Preconditions.checkNotNull(jc);

        logger.trace(jc);

        if (this.getEventBus().hasListeners(JavaClassDefinition.class)) {
            this.getEventBus().publish(new JavaClassDefinition(jc));
        }

        for (final Field field : jc.getFields()) {
            field.accept(this);
        }

        for (final Method method : jc.getMethods()) {
            method.accept(this);
        }
    }

    @Override
    public void visitLineNumber(LineNumber obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {
        logger.trace(obj);
    }

    @Override
    public void visitMethod(final Method method) {
        final MethodGen mg = new MethodGen(method, this.visitedClass.getClassName(), this.cp);

        logger.trace(method);

        if (this.getEventBus().hasListeners(MethodDefinition.class))
            this.getEventBus().publish(new MethodDefinition(mg));

        final MethodVisitor mv = createMethodVisitor(mg);
        mv.start(); //Run visitor for method instructions
    }

    @Override
    public void visitSignature(Signature obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSourceFile(SourceFile obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSynthetic(Synthetic obj) {
        logger.trace(obj);
    }

    @Override
    public void visitUnknown(Unknown obj) {
        logger.trace(obj);
    }

    @Override
    public void visitStackMap(StackMap obj) {
        logger.trace(obj);
    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {
        logger.trace(obj);
    }
}
