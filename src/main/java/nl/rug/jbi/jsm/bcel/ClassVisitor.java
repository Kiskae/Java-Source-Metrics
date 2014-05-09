package nl.rug.jbi.jsm.bcel;

import com.google.common.base.Preconditions;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.generic.ConstantPoolGen;

public class ClassVisitor extends EmptyVisitor {

    private final JavaClass visitedClass;
    private final ConstantPoolGen cp;

    public ClassVisitor(final JavaClass jc) {
        this.visitedClass = jc;
        this.cp = new ConstantPoolGen(this.visitedClass.getConstantPool());
    }

    @Override
    public void visitCode(Code obj) {
    }

    @Override
    public void visitCodeException(CodeException obj) {

    }

    @Override
    public void visitConstantClass(ConstantClass obj) {

    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {

    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {

    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {

    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {

    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {

    }

    @Override
    public void visitConstantLong(ConstantLong obj) {

    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {

    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {

    }

    @Override
    public void visitConstantPool(ConstantPool obj) {

    }

    @Override
    public void visitConstantString(ConstantString obj) {

    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {

    }

    @Override
    public void visitConstantValue(ConstantValue obj) {

    }

    @Override
    public void visitDeprecated(Deprecated obj) {

    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {

    }

    @Override
    public void visitField(Field obj) {

    }

    @Override
    public void visitInnerClass(InnerClass obj) {

    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {

    }

    @Override
    public void visitJavaClass(JavaClass jc) {
        Preconditions.checkNotNull(jc);

        for (final Field field : jc.getFields()) {
            field.accept(this);
        }

        for (final Method method : jc.getMethods()) {
            method.accept(this);
        }
    }

    @Override
    public void visitLineNumber(LineNumber obj) {

    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {

    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {

    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {

    }

    @Override
    public void visitMethod(Method obj) {
        //TODO: create method visitor
    }

    @Override
    public void visitSignature(Signature obj) {

    }

    @Override
    public void visitSourceFile(SourceFile obj) {

    }

    @Override
    public void visitSynthetic(Synthetic obj) {

    }

    @Override
    public void visitUnknown(Unknown obj) {

    }

    @Override
    public void visitStackMap(StackMap obj) {

    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {

    }
}
