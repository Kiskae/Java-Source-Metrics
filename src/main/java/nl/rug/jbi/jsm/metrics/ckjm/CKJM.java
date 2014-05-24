package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.ImmutableSet;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;

import java.util.Set;

public class CKJM implements MetricCollection {
    @Override
    public Set<BaseMetric> getMetrics() {
        return ImmutableSet.<BaseMetric>builder()
                .add(new WMC())
                .add(new NOC())
                .add(new DIT())
                .add(new NPM())
                .add(new RFC())
                .add(new LCOM())
                .add(new CBO())
                .add(new CA())
                .build();
    }
}
