package nl.rug.jbi.jsm.metrics;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.SettableFuture;
import nl.rug.jbi.jsm.core.JSMCore;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import nl.rug.jbi.jsm.frontend.Frontend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkState;

public class TestFrontend implements Frontend {
    private final static Logger logger = LogManager.getLogger(TestFrontend.class);
    private final JSMCore core = new JSMCore();
    private final String packagePrefix;
    private SettableFuture<TestResults> future = null;
    private TestResults results = null;

    public TestFrontend(final String packagePrefix, final BaseMetric... metrics) throws MetricPreparationException {
        this.packagePrefix = packagePrefix;
        for (final BaseMetric metric : metrics) {
            core.registerMetric(metric);
        }
    }

    @Override
    public void init() {
        //Unused for this frontend
    }

    @Override
    public synchronized void processResult(List<MetricResult> resultList) {
        checkState(results != null);

        for (final MetricResult result : resultList) {
            results.putResult(result);
        }
    }

    @Override
    public synchronized void signalDone() {
        checkState(this.results != null);
        checkState(this.future != null);

        this.future.set(this.results);
        this.future = null;
        this.results = null;
    }

    public synchronized Future<TestResults> executeTest(final Set<String> targetClasses) {
        checkState(this.results == null);
        checkState(this.future == null);

        this.results = new TestResults();
        this.future = SettableFuture.create();

        final ImmutableSet<String> classes = FluentIterable.from(targetClasses)
                .transform(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return packagePrefix + s;
                    }
                })
                .toSet();

        logger.debug("Running test for: {}", classes);

        this.core.process(
                this,
                classes
        );

        return this.future;
    }
}
