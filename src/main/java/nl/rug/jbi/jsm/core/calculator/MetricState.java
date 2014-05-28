package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 *
 */
public class MetricState {
    private final Map<String, Object> stateMap = Maps.newHashMap();
    private final String identifier;
    private final Class metricType;

    private Exception executionException = null;

    public MetricState(final String identifier, final Class metricType) {
        this.identifier = identifier;
        this.metricType = metricType;
    }

    public boolean isValid() {
        return this.executionException == null;
    }

    public Exception getExecutionException() {
        return this.executionException;
    }

    public void invalidate(final Exception ex) {
        this.executionException = ex;
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getValue(final String key) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");

        return (T) this.stateMap.get(key);
    }

    /**
     *
     * @param key
     * @param def
     * @param <T>
     * @return
     */
    public <T> T getValue(final String key, final Supplier<T> def) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");
        Preconditions.checkNotNull(def, "The default producer cannot be NULL");

        final T obj = (T) this.stateMap.get(key);
        return (obj != null) ? obj : def.get();
    }

    public <T> T getValueOrCreate(final String key, final Supplier<T> def) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");
        Preconditions.checkNotNull(def, "The default producer cannot be NULL");

        if (this.stateMap.containsKey(key)) {
            return (T) this.stateMap.get(key);
        } else {
            final T newObj = def.get();
            this.stateMap.put(key, newObj);
            return newObj;
        }
    }

    /**
     * @param key
     * @param obj
     */
    public void setValue(final String key, final Object obj) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");
        Preconditions.checkNotNull(obj, "Object to set cannot be NULL, use deleteValue to clear a value.");

        this.stateMap.put(key, obj);
    }

    /**
     * @param key
     * @return
     */
    public boolean deleteValue(final String key) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");
        return stateMap.remove(key) != null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", identifier)
                .add("stateMap", stateMap)
                .add("metricType", metricType)
                .add("executionException", executionException)
                .toString();
    }
}
