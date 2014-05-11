package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Preconditions;
import nl.rug.jbi.jsm.core.EventBus;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassVisitor extends EmptyVisitor {
    private final static Logger logger = LogManager.getLogger(ClassVisitor.class);

    private final org.apache.bcel.classfile.JavaClass visitedClass;
    private final EventBus eBus;
    private final ConstantPoolGen cp;

    public ClassVisitor(final org.apache.bcel.classfile.JavaClass jc, final EventBus eBus) {
        this.visitedClass = jc;
        this.eBus = eBus;
        this.cp = new ConstantPoolGen(this.visitedClass.getConstantPool());
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
    public void visitJavaClass(org.apache.bcel.classfile.JavaClass jc) {
        Preconditions.checkNotNull(jc);

        logger.trace(jc);

        if (this.eBus.hasListeners(JavaClass.class))
            this.eBus.publish(new JavaClass(jc));

        for (final Field field : jc.getFields()) {
            field.accept(this);
        }

        for (final org.apache.bcel.classfile.Method method : jc.getMethods()) {
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
    public void visitMethod(final org.apache.bcel.classfile.Method method) {
        final MethodGen mg = new MethodGen(method, this.visitedClass.getClassName(), this.cp);

        logger.trace(method);

        if (this.eBus.hasListeners(Method.class))
            this.eBus.publish(new Method(mg));

        final MethodVisitor mv = new MethodVisitor(mg, this.eBus);
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
