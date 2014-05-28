package nl.rug.jbi.jsm.bcel;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;

/**
 * Utility class exposing methods that are useful for processing the BCEL data objects into data usable by metrics.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class BCELTools {

    /**
     * Converts any type into the correct string representing the underlying type.
     * It will unroll ArrayTypes and turn primitive types into a common type "java.PRIMITIVE".
     * Based on the implementation by dspinellis
     *
     * @param t BCEL Type object
     * @return String representation of the base type of the provided type object.
     * @see <a href="https://github.com/dspinellis/ckjm/blob/master/src/gr/spinellis/ckjm/ClassVisitor.java#L180">
     * Original Implementation</a>
     */
    public static String type2className(final Type t) {
        final String className = t.toString();

        if (t.getType() <= Constants.T_VOID) {
            return "java.PRIMITIVE";
        } else if (t instanceof ArrayType) {
            return type2className(((ArrayType) t).getBasicType());
        } else {
            return className;
        }
    }
}
