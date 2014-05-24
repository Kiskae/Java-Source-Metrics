package nl.rug.jbi.jsm.util;

import com.google.common.base.Objects;

/**
 * @param <F>
 * @param <S>
 * @author David van Leusen
 * @since 1.0
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
