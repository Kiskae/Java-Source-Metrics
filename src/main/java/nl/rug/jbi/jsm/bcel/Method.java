package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.MethodGen;

public class Method {
    private final MethodGen mg;

    public Method(MethodGen mg) {
        this.mg = mg;
    }

    public String getSignature() {
        return mg.getSignature();
    }

    public String getMethodName() {
        return mg.getName();
    }

    public String getReturnType() {
        return mg.getReturnType().toString();
    }

    public String[] getExceptions() {
        return mg.getExceptions();
    }

    public String[] getArgumentTypes() {
        return mg.getArgumentNames();
    }
}
