package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ObjectType;

/**
 * Represents a catch-block of a try-catch structure, allowing inspection of the caught exception.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class ExceptionHandlerDefinition {
    private final CodeExceptionGen ceg;

    public ExceptionHandlerDefinition(final CodeExceptionGen ceg) {
        this.ceg = ceg;
    }

    /**
     * Can return NULL, indicating it catches ANYTHING
     *
     * @return String representing the type of exception caught, or NULL.
     */
    public String getCatchType() {
        final ObjectType catchType = this.ceg.getCatchType();
        return catchType != null ? catchType.getClassName() : null;
    }
}
