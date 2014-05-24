package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableSet;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;

import java.util.Set;

public class PackageMetrics implements MetricCollection {
    @Override
    public Set<BaseMetric> getMetrics() {
        return ImmutableSet.<BaseMetric>builder()
                .add(new PF())
                .add(new IIPU())
                .add(new IIPE())
                .add(new IPCI())
                .add(new IIPUD())
                .add(new IIPED())
                .add(new IPSC())
                .build();
    }
}
