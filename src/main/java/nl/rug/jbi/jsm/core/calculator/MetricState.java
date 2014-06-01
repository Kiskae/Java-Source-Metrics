package nl.rug.jbi.jsm.core.calculator;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Container for any state that needs to be maintained whilst evaluating the results of a given identifier-metric pair.
 * Metrics should maintain their state within this class to ensure thread-safe evaluation.
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
public class MetricState {
    private final Map<String, Object> stateMap = Maps.newHashMap();
    private final String identifier;
    private final Class metricType;
    private Exception executionException = null;

    /**
     * Create a new metric state for the given identifier-metric pair.
     *
     * @param identifier Name of the resource that will be evaluated with the state in this object.
     * @param metric     Metric that will evaluate the resource.
     */
    public MetricState(final String identifier, final Class metric) {
        this.identifier = identifier;
        this.metricType = metric;
    }

    /**
     * Whether this state is still considered consistent, if any exception occurs during evaluation the state is
     * considered undefined.
     *
     * @return Whether the state is still considered consistent.
     */
    public boolean isValid() {
        return this.executionException == null;
    }

    /**
     * @return The exception that has caused this state to invalidate, if any.
     * Will return NULL if {@link #isValid()} returns true.
     */
    public Exception getExecutionException() {
        return this.executionException;
    }

    /**
     * Invalidate this state with the given exception, once invalidated this state will cease evaluating and either
     * return an {@link nl.rug.jbi.jsm.core.execution.InvalidResult} if the metric is isolated or accumulate the
     * 'invalidMembers' count for shared metrics or producers.
     *
     * @param ex Exception that has caused invalidation.
     * @throws java.lang.NullPointerException if ex is NULL
     */
    public void invalidate(final Exception ex) {
        this.executionException = checkNotNull(ex);
    }

    /**
     * @return The identifier of the resource that this state is tied to.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Get a value from the internal map, use of {@link #getValue(String, com.google.common.base.Supplier)} or
     * {@link #getValueOrCreate(String, com.google.common.base.Supplier)} is recommended.
     **
     * @param key Unique key by which the value can be identified.
     * @param <T> Type of the given object.
     * @return The object if it exists within this state, otherwise NULL
     * @throws java.lang.ClassCastException if the stored object has an incompatible type.
     */
    public <T> T getValue(final String key) {
        checkNotNull(key, "Key cannot be NULL");

        return (T) this.stateMap.get(key);
    }

    /**
     * Get a value from the internal store, if that value doesn't exist the result from the given
     * {@link com.google.common.base.Supplier#get()} is returned.
     **
     * @param key Unique key by which the value can be identified.
     * @param def Default value supplier if the value doesn't exist within the store.
     * @param <T> Type of the given object.
     * @return The associated value from the store or the default given by the supplier.
     */
    public <T> T getValue(final String key, final Supplier<T> def) {
        checkNotNull(key, "Key cannot be NULL");
        checkNotNull(def, "The default producer cannot be NULL");

        final T obj = (T) this.stateMap.get(key);
        return (obj != null) ? obj : def.get();
    }

    /**
     * Same as {@link #getValue(String, com.google.common.base.Supplier)}, but if the value is missing the default value
     * is immediately inserted into the store at the same time.
     * *
     *
     * @param key Unique key by which the value can be identified.
     * @param def Default value supplier if the value doesn't exist within the store.
     * @param <T> Type of the given object.
     * @return The associated value from the store or the default given by the supplier.
     */
    public <T> T getValueOrCreate(final String key, final Supplier<T> def) {
        checkNotNull(key, "Key cannot be NULL");
        checkNotNull(def, "The default producer cannot be NULL");

        if (this.stateMap.containsKey(key)) {
            return (T) this.stateMap.get(key);
        } else {
            final T newObj = def.get();
            this.stateMap.put(key, newObj);
            return newObj;
        }
    }

    /**
     * Put the key-value pair into the internal map.
     *
     * @param key Unique key by which the value can be retrieved.
     * @param obj Value to put in the internal map.
     * @throws java.lang.NullPointerException if key or obj is NULL, use {@link #deleteValue(String)} to remove values.
     */
    public void setValue(final String key, final Object obj) {
        checkNotNull(key, "Key cannot be NULL");
        checkNotNull(obj, "Object to set cannot be NULL, use deleteValue to clear a value.");

        this.stateMap.put(key, obj);
    }

    /**
     * Remove a the given key from the internal map along with its attached value.
     *
     * @param key Unique key by which the value can be identified.
     * @return Whether the key was successfully removed.
     */
    public boolean deleteValue(final String key) {
        checkNotNull(key, "Key cannot be NULL");
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
