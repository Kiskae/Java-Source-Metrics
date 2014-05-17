package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

public class FieldAccessInstr {
    private final FieldInstruction instruction;
    private final ConstantPoolGen cp;

    public FieldAccessInstr(final FieldInstruction instruction, final ConstantPoolGen cp) {
        this.instruction = instruction;
        this.cp = cp;
    }

    public String getClassName() {
        return type2className(this.instruction.getReferenceType(this.cp));
    }

    public String getFieldName() {
        return this.instruction.getFieldName(this.cp);
    }

    public String getFieldType() {
        return type2className(this.instruction.getFieldType(this.cp));
    }
}
