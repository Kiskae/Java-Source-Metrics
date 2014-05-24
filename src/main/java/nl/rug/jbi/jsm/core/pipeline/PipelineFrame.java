package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.calculator.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class PipelineFrame {
    private final MetricScope scope;
    private final Set<Class> availableData = Sets.newHashSet();
    private final List<IsolatedMetric> isolatedMetrics = Lists.newLinkedList();
    private final List<SharedMetric> sharedMetrics = Lists.newLinkedList();
    private final List<ProducerMetric> producerMetrics = Lists.newLinkedList();
    private PipelineFrame nextFrame = null;

    public PipelineFrame(final MetricScope scope) {
        this.scope = checkNotNull(scope);
    }

    public PipelineFrame(final MetricScope scope, final Set<Class> initData) {
        this(scope);
        this.availableData.addAll(
                checkNotNull(initData)
        );
    }

    public PipelineFrame(final PipelineFrame previousFrame) {
        this(checkNotNull(previousFrame).getScope());
        this.availableData.addAll(previousFrame.availableData);
        previousFrame.setNextFrame(this);
    }

    public void addDataClass(final Class dataClass) {
        this.availableData.add(dataClass);
        if (this.nextFrame != null) {
            this.nextFrame.addDataClass(dataClass);
        }
    }

    public MetricScope getScope() {
        return this.scope;
    }

    public boolean checkAvailableData(final Set<Class> requiredData) {
        return this.availableData.containsAll(requiredData);
    }

    public PipelineFrame getNextFrame() {
        return this.nextFrame;
    }

    private void setNextFrame(final PipelineFrame nextFrame) {
        this.nextFrame = nextFrame;
    }

    public List<IsolatedMetric> getIsolatedMetrics() {
        return Collections.unmodifiableList(this.isolatedMetrics);
    }

    public List<SharedMetric> getSharedMetrics() {
        return Collections.unmodifiableList(this.sharedMetrics);
    }

    public List<ProducerMetric> getProducerMetrics() {
        return Collections.unmodifiableList(this.producerMetrics);
    }

    public void registerMetric(final BaseMetric metric) throws MetricPreparationException {
        if (metric instanceof IsolatedMetric) {
            this.isolatedMetrics.add((IsolatedMetric) metric);
        } else if (metric instanceof SharedMetric) {
            this.sharedMetrics.add((SharedMetric) metric);
        } else if (metric instanceof ProducerMetric) {
            this.producerMetrics.add((ProducerMetric) metric);
        } else {
            throw new MetricPreparationException("Unknown metric subtype: " + metric.getClass().getName(), null);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("scope", scope)
                .add("availableData", availableData)
                .add("nextFrame", nextFrame)
                .add("isolatedMetrics", isolatedMetrics)
                .add("sharedMetrics", sharedMetrics)
                .add("producerMetrics", producerMetrics)
                .toString();
    }
}
