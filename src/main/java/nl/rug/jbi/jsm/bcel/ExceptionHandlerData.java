package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ObjectType;

public class ExceptionHandlerData {
    private final CodeExceptionGen ceg;

    public ExceptionHandlerData(final CodeExceptionGen ceg) {
        this.ceg = ceg;
    }

    public String getCatchType() {
        final ObjectType catchType = this.ceg.getCatchType();
        return catchType != null ? catchType.getClassName() : null;
    }
}
