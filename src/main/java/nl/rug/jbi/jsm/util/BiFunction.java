package nl.rug.jbi.jsm.util;

/**
 * BiFunction is a variant of {@link com.google.common.base.Function} that takes two arguments.
 * In its basic form it represents a function with two parameters that returns a value.
 *
 * @param <A> Input type 1
 * @param <B> Input type 2
 * @param <C> Output type
 * @author David van Leusen
 * @see com.google.common.base.Function
 * @since 2014-05-28
 */
public interface BiFunction<A, B, C> {

    /**
     * Returns the result of applying this function to the given inputs. This method is <i>generally expected</i>, but
     * not absolutely required, to have the following properties:
     * <ul>
     * <li>Its execution does not cause any observable side effects.</li>
     * <li>The computation is consistent with equals; that is, {@code Objects.equal(a, b)} and
     * {@code Objects.equal(c, d)} implies that {@code Objects.equal(function.apply(a, c), function.apply(b, d))}.</li>
     * </ul>
     *
     * @param input1 First input
     * @param input2 Second input
     * @return A value based on input1 and input2
     * @throws java.lang.NullPointerException if {@code input1} or {@code input2} is null and this function does not
     *                                        accept null arguments
     */
    C apply(A input1, B input2);
}
