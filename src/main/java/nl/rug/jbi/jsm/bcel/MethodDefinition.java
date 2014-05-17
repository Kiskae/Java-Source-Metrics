package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.MethodGen;

public class MethodDefinition {
    private final MethodGen mg;

    public MethodDefinition(MethodGen mg) {
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

    public boolean isPublic() {
        return mg.isPublic();
    }

    public boolean isProtected() {
        return mg.isProtected();
    }

    public boolean isAbstract() {
        return mg.isAbstract();
    }

    public boolean isPrivate() {
        return mg.isPrivate();
    }
}
