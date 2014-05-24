package nl.rug.jbi.jsm.metrics;

import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;

import java.util.Map;

public class TestResults {
    public final static String COLLECTION_IDENTIFIER = "Bootstrap";
    private final Map<MetricScope, Table<String, Class, Object>> results = Maps.newEnumMap(MetricScope.class);

    public TestResults() {
        this.results.put(MetricScope.CLASS, HashBasedTable.<String, Class, Object>create());
        this.results.put(MetricScope.PACKAGE, HashBasedTable.<String, Class, Object>create());
        this.results.put(MetricScope.COLLECTION, HashBasedTable.<String, Class, Object>create());
    }

    public Object getResult(final MetricScope scope, final String identifier, final Class metric) {
        return this.results.get(scope).get(identifier, metric);
    }

    public double getDoubleResult(final MetricScope scope, final String identifier, final Class metric) {
        return ((Number) getResult(scope, identifier, metric)).doubleValue();
    }

    public Map<String, Object> getMetricResults(final MetricScope scope, final Class metric) {
        return this.results.get(scope).column(metric);
    }

    void putResult(final MetricResult result) {
        this.results.get(result.getScope()).put(result.getIdentifier(), result.getMetricClass(), result.getValue());
    }

    public boolean hasResults(final MetricScope scope, final String identifier, final Class metric) {
        return this.results.get(scope).contains(identifier, metric);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("results", results)
                .toString();
    }
}
