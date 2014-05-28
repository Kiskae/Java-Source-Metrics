package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;

import static nl.rug.jbi.jsm.bcel.BCELTools.type2className;

/**
 * Represents access to the field of a class, this can be any class for which the field is accessible from the class
 * being inspected in the current scope.
 *
 * @author David van Leusen
 * @since 2014-05-28
 */
public class FieldAccessInstr {
    private final FieldInstruction instruction;
    private final ConstantPoolGen cp;

    public FieldAccessInstr(final FieldInstruction instruction, final ConstantPoolGen cp) {
        this.instruction = instruction;
        this.cp = cp;
    }

    /**
     * @return String representing the class that the referenced field belongs to.
     */
    public String getClassName() {
        return type2className(this.instruction.getReferenceType(this.cp));
    }

    /**
     * @return Name of the referenced field.
     */
    public String getFieldName() {
        return this.instruction.getFieldName(this.cp);
    }

    /**
     * @return String representing the type of the referenced field.
     */
    public String getFieldType() {
        return type2className(this.instruction.getFieldType(this.cp));
    }
}
