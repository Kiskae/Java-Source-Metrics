package nl.rug.jbi.jsm.util;

/**
 * @param <A> Input type 1
 * @param <B> Input type 2
 * @param <C> Output type
 * @author David van Leusen
 * @see com.google.common.base.Function
 * @since 1.0
 */
public interface BiFunction<A, B, C> {

    /**
     * Applies a function to in1 and in2 to create some output.
     *
     * @param in1
     * @param in2
     * @return
     */
    C apply(A in1, B in2);
}
