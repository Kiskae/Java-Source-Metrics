package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Metric calculator for the Index of Inter-Package Extending (IIPE)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IIPE extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IIPE.class);

    public IIPE() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("Extends", pack.ExtSum());
        state.setValue("ExternalExtends", pack.ExtC());
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.COLLECTION);
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IIPE: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final double packageExtends = FluentIterable.from(states.values())
                .transformAndConcat(new Function<MetricState, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(MetricState metricState) {
                        return metricState.<Set<String>>getValue("ExternalExtends");
                    }
                })
                .toSet().size();

        final int globalExtends = FluentIterable.from(states.values())
                .transformAndConcat(new Function<MetricState, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(MetricState metricState) {
                        return metricState.<Set<String>>getValue("Extends");
                    }
                })
                .toSet().size();

        //packageExtends = ExtSum(P)
        //globalExtends = ExtSum(C)
        //TODO: fix for different collections
        return ImmutableList.of(new MetricResult(
                "Cptn. Placeholder",
                IIPE.class,
                MetricScope.COLLECTION,
                1 - (globalExtends != 0 ? packageExtends / globalExtends : 0)
        ));
    }
}
