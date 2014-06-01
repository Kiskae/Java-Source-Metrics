package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.ImmutableSet;
import nl.rug.jbi.jsm.core.calculator.BaseMetric;
import nl.rug.jbi.jsm.core.calculator.MetricCollection;

import java.util.Set;

/**
 * Implementation of the metrics described by Chidamber and Kemerer in
 * <a href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=295895">A Metrics Suite for Object Oriented
 * Design</a>.
 * Implementation based on the implementation by Diomidis Spinellis: <a href="https://github.com/dspinellis/ckjm">
 * dspinellis/ckjm</a>
 *
 * @author David van Leusen
 * @since 2014-06-01
 */
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
