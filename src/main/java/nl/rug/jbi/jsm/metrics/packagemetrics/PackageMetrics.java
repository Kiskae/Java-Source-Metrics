package nl.rug.jbi.jsm.metrics.packagemetrics;

import com.google.common.collect.ImmutableSet;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;

import java.util.Set;

/**
 * Implementation of the metrics suite described by Abdeen, Ducasse and Sahraoui in
 * <a href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6079866">Modularization Metrics: Assessing
 * Package Organization in Legacy Large Object-Oriented Software</a>
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
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
