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
 * Metric calculator for the Index of Inter-Package Extending Diversion (IIPED)
 *
 * @author David van Leusen
 * @since 1.0
 */
public class IIPED extends SharedMetric {

    public IIPED() {
        super(MetricScope.PACKAGE);
    }

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final PackageUnit pack) {
        final int ExtC = pack.ExtC().size();
        final double result;
        if (ExtC != 0) {
            final double Ext = pack.Ext().size();
            result = (1 / Ext) * (1 - ((Ext - 1) / ExtC));
        } else {
            result = 1.0;
        }

        state.setValue("IIPED-p", result);
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
                        final double result = entry.getValue().getValue("IIPED-p");

                        acc.addAndGet(result);

                        return new MetricResult(
                                entry.getKey(),
                                IIPED.this,
                                result
                        );
                    }
                })
                .copyInto(results);

        //TODO: fix for different collections
        results.add(new MetricResult(
                "Cptn. Placeholder",
                IIPED.class,
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
