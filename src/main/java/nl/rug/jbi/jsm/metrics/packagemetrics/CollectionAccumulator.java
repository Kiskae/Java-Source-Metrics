package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.AtomicDouble;
import nl.rug.jbi.jsm.util.DefaultValue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class CollectionAccumulator {
    public final static DefaultValue<AtomicInteger> DEF_ATOMIC_INT = new DefaultValue<AtomicInteger>() {
        @Override
        public AtomicInteger getDefault() {
            return new AtomicInteger(0);
        }
    };
    public final static DefaultValue<Set<String>> DEF_SET_STRING = new DefaultValue<Set<String>>() {
        @Override
        public Set<String> getDefault() {
            return Sets.newHashSet();
        }
    };
    public final static DefaultValue<AtomicDouble> DEF_ATOMIC_DOUBLE = new DefaultValue<AtomicDouble>() {
        @Override
        public AtomicDouble getDefault() {
            return new AtomicDouble(0.0);
        }
    };

    private final Table<String, String, Object> dataTable = HashBasedTable.create();

    public <T> T getOrSet(final String collectionId, final String dataId, final DefaultValue<T> def) {
        if (this.dataTable.contains(collectionId, dataId)) {
            //noinspection unchecked
            return (T) this.dataTable.get(collectionId, dataId);
        } else {
            final T newObj = def.getDefault();
            this.dataTable.put(collectionId, dataId, newObj);
            return newObj;
        }
    }

    public Set<Map.Entry<String, Map<String, Object>>> getEntrySetByCollection() {
        return dataTable.rowMap().entrySet();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("dataTable", this.dataTable)
                .toString();
    }
}
