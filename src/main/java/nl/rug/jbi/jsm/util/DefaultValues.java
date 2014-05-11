package nl.rug.jbi.jsm.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Companion to the {@link nl.rug.jbi.jsm.util.DefaultValue} class, this class provides some generic default values for
 * use in the project.
 *
 * @author David van Leusen
 * @see nl.rug.jbi.jsm.util.DefaultValue
 * @since 1.0
 */
public class DefaultValues {
    /**
     * Provides a default value of {@code 0} as an {@link java.lang.Integer}
     */
    public static final DefaultValue<Integer> ZERO_INT = new DefaultValue<Integer>() {
        @Override
        public Integer getDefault() {
            return 0;
        }
    };

    /**
     * Provides a default value for an empty list with a certain generic type.
     *
     * @param <T> Type that the list should contain.
     * @return An empty list
     */
    public static <T> DefaultValue<List<T>> emptyList() {
        return new DefaultValue<List<T>>() {
            @Override
            public List<T> getDefault() {
                return Lists.newLinkedList();
            }
        };
    }

    private DefaultValues() {
        throw new IllegalStateException("DefaultValues cannot be instantiated");
    }
}
