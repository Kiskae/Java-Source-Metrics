package nl.rug.jbi.jsm.metrics;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import nl.rug.jbi.jsm.util.CompositeBCELClassLoader;
import nl.rug.jbi.jsm.bcel.JavaClassDefinition;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.core.calculator.MetricState;
import nl.rug.jbi.jsm.core.calculator.ProducerMetric;
import nl.rug.jbi.jsm.core.event.Subscribe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkState;

/**
 * Producer for {@link nl.rug.jbi.jsm.metrics.ClassSource}
 *
 * @author David van Leusen
 * @since 2014-05-29
 */
public class ClassSourceProducer extends ProducerMetric {
    private final static Supplier<String> MISSING_SOURCE = new Supplier<String>() {
        @Override
        public String get() {
            return "UNKNOWN";
        }
    };

    private static AtomicReference<CompositeBCELClassLoader> CLOADER = new AtomicReference<CompositeBCELClassLoader>(null);

    public ClassSourceProducer() {
        super(MetricScope.CLASS, MetricScope.CLASS);
    }

    public static void setCBCL(final CompositeBCELClassLoader cLoader) {
        CLOADER = new AtomicReference<CompositeBCELClassLoader>(cLoader);
    }

    @Subscribe
    public void onClass(final MetricState state, final JavaClassDefinition ignored) {
        final CompositeBCELClassLoader CBCL = CLOADER.get();
        if (CBCL != null) {
            state.setValue("source", CBCL.getSource(state.getIdentifier()));
        }
    }

    @Override
    public List<Produce> getProduce(final Map<String, MetricState> states, final int invalidMembers) {
        checkState(invalidMembers == 0, "Calculation of this producer should never fail.");

        final List<Produce> ret = Lists.newLinkedList();

        for (final Map.Entry<String, MetricState> entry : states.entrySet()) {
            final String source = entry.getValue().getValue("source", MISSING_SOURCE);
            ret.add(new Produce(entry.getKey(), new ClassSource(entry.getKey(), source)));
        }

        return ret;
    }

    @Override
    public Class getProducedClass() {
        return ClassSource.class;
    }
}
