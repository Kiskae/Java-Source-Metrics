package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Preconditions;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassVisitor extends EmptyVisitor {
    private final static Logger logger = LogManager.getLogger(ClassVisitor.class);

    private final JavaClass visitedClass;
    private final ConstantPoolGen cp;

    public ClassVisitor(final JavaClass jc) {
        this.visitedClass = jc;
        this.cp = new ConstantPoolGen(this.visitedClass.getConstantPool());
    }

    public void start() {
        this.visitJavaClass(this.visitedClass);
    }

    @Override
    public void visitCode(Code obj) {
        logger.debug(obj);
    }

    @Override
    public void visitCodeException(CodeException obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantLong(ConstantLong obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantPool(ConstantPool obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantString(ConstantString obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantValue(ConstantValue obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDeprecated(Deprecated obj) {
        logger.debug(obj);
    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {
        logger.debug(obj);
    }

    @Override
    public void visitField(Field obj) {
        logger.debug(obj);
    }

    @Override
    public void visitInnerClass(InnerClass obj) {
        logger.debug(obj);
    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {
        logger.debug(obj);
    }

    @Override
    public void visitJavaClass(JavaClass jc) {
        Preconditions.checkNotNull(jc);

        //TODO: javaclass publish

        for (final Field field : jc.getFields()) {
            field.accept(this);
        }

        for (final org.apache.bcel.classfile.Method method : jc.getMethods()) {
            method.accept(this);
        }
    }

    @Override
    public void visitLineNumber(LineNumber obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {
        logger.debug(obj);
    }

    @Override
    public void visitMethod(final org.apache.bcel.classfile.Method method) {
        final MethodGen mg = new MethodGen(method, this.visitedClass.getClassName(), this.cp);

        //TODO: publish
        new Method(mg);

        final MethodVisitor mv = new MethodVisitor(mg);
        mv.start(); //Run visitor for method instructions
    }

    @Override
    public void visitSignature(Signature obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSourceFile(SourceFile obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSynthetic(Synthetic obj) {
        logger.debug(obj);
    }

    @Override
    public void visitUnknown(Unknown obj) {
        logger.debug(obj);
    }

    @Override
    public void visitStackMap(StackMap obj) {
        logger.debug(obj);
    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {
        logger.debug(obj);
    }
}
