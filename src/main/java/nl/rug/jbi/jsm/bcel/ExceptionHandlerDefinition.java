package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ObjectType;

public class ExceptionHandlerDefinition {
    private final CodeExceptionGen ceg;

    public ExceptionHandlerDefinition(final CodeExceptionGen ceg) {
        this.ceg = ceg;
    }

    /**
     * Can return NULL, indicating it catches ANYTHING
     *
     * @return
     */
    public String getCatchType() {
        final ObjectType catchType = this.ceg.getCatchType();
        return catchType != null ? catchType.getClassName() : null;
    }
}
