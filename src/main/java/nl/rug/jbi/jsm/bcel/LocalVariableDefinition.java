package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.LocalVariableGen;

public class LocalVariableDefinition {
    private final LocalVariableGen localVariable;

    public LocalVariableDefinition(final LocalVariableGen localVariable) {
        this.localVariable = localVariable;
    }

    public String getName() {
        return this.localVariable.getName();
    }

    public String getType() {
        return BCELTools.type2className(this.localVariable.getType());
    }
}
