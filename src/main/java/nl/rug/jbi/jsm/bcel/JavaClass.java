package nl.rug.jbi.jsm.bcel;

public class JavaClass {

    private final org.apache.bcel.classfile.JavaClass jc;

    public JavaClass(final org.apache.bcel.classfile.JavaClass jc) {
        this.jc = jc;
    }

    public String getClassName() {
        return this.jc.getClassName();
    }
}
