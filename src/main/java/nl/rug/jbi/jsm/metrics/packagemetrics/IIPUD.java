package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import nl.rug.jbi.jsm.core.calculator.MetricResult;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.SharedMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageProducer;
import nl.rug.jbi.jsm.metrics.packagemetrics.resource.PackageUnit;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Metric calculator for the Index of Inter-Package Usage Diversion (IIPUD)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IIPUD extends SharedMetric {

    public IIPUD() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final int UsesC = pack.UsesC().size();
        final double result;
        if (UsesC != 0) {
            final double Uses = pack.Uses().size();
            result = (1 / Uses) * (1 - ((Uses - 1) / UsesC));
        } else {
            result = 1.0;
        }

        state.setValue("IIPUD-p", result);
    }

    public double getSingleResult(String identifier, MetricState state) {
        return state.getValue("IIPUD-p");
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
        //TODO: check invalidMembers, output warning if != 0

        final List<MetricResult> results = Lists.newLinkedList();

        final AtomicDouble acc = new AtomicDouble(0);

        FluentIterable.from(states.entrySet())
                .transform(new Function<Map.Entry<String, MetricState>, MetricResult>() {
                    @Override
                    public MetricResult apply(final Map.Entry<String, MetricState> entry) {
                        final double result = getSingleResult(entry.getKey(), entry.getValue());

                        acc.addAndGet(result);

                        return new MetricResult(
                                entry.getKey(),
                                IIPUD.this,
                                result
                        );
                    }
                })
                .copyInto(results);

        results.add(new MetricResult(
                "Cptn. Placeholder",
                IIPUD.class,
                MetricScope.COLLECTION,
                acc.doubleValue() / states.size()
        ));

        return results;
    }

    @Override
    public EnumSet<MetricScope> getResultScopes() {
        return EnumSet.of(MetricScope.PACKAGE, MetricScope.COLLECTION);
    }
}
