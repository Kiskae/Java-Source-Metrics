package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.Type;

/**
 * Represents the use of a type not bound to a specific invocation. The current {@link nl.rug.jbi.jsm.bcel.ClassVisitor}
 * emits this data on INSTANCEOF, CAST and RETURN.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class TypeUseInstruction {
    private final String typeName;

    public TypeUseInstruction(final Type t) {
        this.typeName = BCELTools.type2className(t);
    }

    /**
     * @return String representation of the referenced type.
     */
    public String getTypeName() {
        return this.typeName;
    }
}
