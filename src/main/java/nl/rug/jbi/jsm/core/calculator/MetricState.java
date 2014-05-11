package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import nl.rug.jbi.jsm.util.DefaultValue;

import java.util.Map;

/**
 *
 */
public class MetricState {
    private final Map<String, Object> stateMap = Maps.newHashMap();
    private final String identifier;
    private final Class metricType;

    //TODO: flag for exception during execution

    public MetricState(final String identifier, final Class metricType) {
        this.identifier = identifier;
        this.metricType = metricType;
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
     * @param key
     * @param def
     * @param <T>
     * @return
     */
    public <T> T getValue(final String key, final DefaultValue<T> def) {
        Preconditions.checkNotNull(key, "Key cannot be NULL");
        Preconditions.checkNotNull(def, "The default producer cannot be NULL");

        final T obj = (T) this.stateMap.get(key);
        return (obj != null) ? obj : def.getDefault();
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
                .toString();
    }
}
