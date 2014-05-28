package nl.rug.jbi.jsm.util;

import com.google.common.base.Objects;

/**
 * Represents a pair of data.
 *
 * @param <F> Type of first item
 * @param <S> Type of second item
 * @author David van Leusen
 * @since 2014-05-28
 */
public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("first", first)
                .add("second", second)
                .toString();
    }
}
