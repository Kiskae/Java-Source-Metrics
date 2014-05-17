package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.Type;

public class TypeUseInstruction {
    private final String typeName;

    public TypeUseInstruction(final Type t) {
        this.typeName = BCELTools.type2className(t);
    }

    public String getTypeName() {
        return this.typeName;
    }
}
