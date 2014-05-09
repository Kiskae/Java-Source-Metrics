package nl.rug.jbi.jsm.util;

import com.google.common.collect.Lists;

import java.util.List;

public class DefaultValues {
    public static final DefaultValue<Integer> ZERO_INT = new DefaultValue<Integer>() {
        @Override
        public Integer getDefault() {
            return 0;
        }
    };

    public static <T> DefaultValue<List<T>> emptyList() {
        return new DefaultValue<List<T>>() {
            @Override
            public List<T> getDefault() {
                return Lists.newLinkedList();
            }
        };
    }
}
