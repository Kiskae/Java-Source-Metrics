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
 * Metric calculator for the Index of Inter-Package Usage (IIPU)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IIPU extends SharedMetric {
    private final static Logger logger = LogManager.getLogger(IIPU.class);

    public IIPU() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        state.setValue("Collection", pack.getSourceIdentifier());
        state.setValue("Uses", pack.UsesSum());
        state.setValue("ExternalUses", pack.UsesC());
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.COLLECTION);
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        if (invalidMembers != 0) {
            logger.warn(
                    "IIPU: Unsuccessful calculation for {} package(s), collection results might be inaccurate.",
                    invalidMembers
            );
        }

        final double packageUses = FluentIterable.from(states.values())
                .transformAndConcat(new Function<MetricState, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(MetricState metricState) {
                        return metricState.<Set<String>>getValue("ExternalUses");
                    }
                })
                .toSet().size();

        final int globalUses = FluentIterable.from(states.values())
                .transformAndConcat(new Function<MetricState, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(MetricState metricState) {
                        return metricState.<Set<String>>getValue("Uses");
                    }
                })
                .toSet().size();

        //packageExtends = UsesSum(P)
        //globalExtends = UsesSum(C)
        //TODO: fix for different collections
        return ImmutableList.of(new MetricResult(
                "Cptn. Placeholder",
                IIPU.class,
                MetricScope.COLLECTION,
                1 - (globalUses != 0 ? packageUses / globalUses : 0)
        ));
    }
}
