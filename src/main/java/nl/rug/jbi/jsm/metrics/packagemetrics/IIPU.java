package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import nl.rug.jbi.jsm.core.calculator.*;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.util.DefaultValues;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the Index of Inter-Package Usage (IIPU) for all packages
 */
public class IIPU extends SharedMetric<MetricResult> {

    @Subscribe
    @UsingProducer(PackageProducer.class)
    public void onPackage(final MetricState state, final Package pack) {
        final Set<String> externalClasses = state.getValue("ExternalClasses", DefaultValues.<String>emptySet());
        final int packageNameLen = pack.getPackageName().length() + 1;
        externalClasses.addAll(Collections2.filter(pack.getUsedClasses(), new Predicate<String>() {
            @Override
            public boolean apply(final String className) {
                return className.length() > packageNameLen && Character.isUpperCase(className.codePointAt(packageNameLen));
            }
        }));
        state.setValue("ExternalClasses", externalClasses);
    }

    @Override
    public List<MetricResult> getResults(Map<String, MetricState> states) {
        return null;
    }
}
