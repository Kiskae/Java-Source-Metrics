package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.LocalVariableGen;

/**
 * Represents a local variable declared in a method.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class LocalVariableDefinition {
    private final LocalVariableGen localVariable;

    public LocalVariableDefinition(final LocalVariableGen localVariable) {
        this.localVariable = localVariable;
    }

    /**
     * @return Name of the local variable.
     */
    public String getName() {
        return this.localVariable.getName();
    }

    /**
     * @return String representing the type of the local variable.
     */
    public String getType() {
        return BCELTools.type2className(this.localVariable.getType());
    }
}
