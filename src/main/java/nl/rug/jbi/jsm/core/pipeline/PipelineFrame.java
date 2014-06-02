package nl.rug.jbi.jsm.core.pipeline;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.rug.jbi.jsm.core.calculator.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A frame of execution, it describes a set of data that is available within this frame and contains lists of metrics
 * that are ready for finalization within this frame. Each frame represents a frame of execution in
 * {@link nl.rug.jbi.jsm.core.execution.PipelineExecutor}.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class PipelineFrame {
    private final MetricScope scope;
    private final Set<Class> availableData = Sets.newHashSet();
    private final List<IsolatedMetric> isolatedMetrics = Lists.newLinkedList();
    private final List<SharedMetric> sharedMetrics = Lists.newLinkedList();
    private final List<ProducerMetric> producerMetrics = Lists.newLinkedList();
    private PipelineFrame nextFrame = null;

    PipelineFrame(final MetricScope scope) {
        this.scope = checkNotNull(scope);
    }

    PipelineFrame(final MetricScope scope, final Set<Class> initData) {
        this(scope);
        this.availableData.addAll(
                checkNotNull(initData)
        );
    }

    PipelineFrame(final PipelineFrame previousFrame) {
        this(checkNotNull(previousFrame).getScope(), previousFrame.availableData);
        previousFrame.setNextFrame(this);
    }

    /**
     * Adds a type of data that will be available in this frame AND all subsequent frames.
     *
     * @param dataClass Data to register
     */
    public void addDataClass(final Class dataClass) {
        this.availableData.add(dataClass);
        if (this.nextFrame != null) {
            this.nextFrame.addDataClass(dataClass);
        }
    }

    /**
     * @return The scope in which this frame will be evaluated.
     */
    public MetricScope getScope() {
        return this.scope;
    }

    /**
     * Checks whether this frame has all the given data available. The set of data available in this frame is determined
     * by the base set of data in {@link nl.rug.jbi.jsm.core.pipeline.Pipeline} and the results of any producers in
     * previous frames.
     *
     * @param requiredData The set of data that needs to be available.
     * @return Whether said data is available.
     */
    public boolean checkAvailableData(final Set<Class> requiredData) {
        return this.availableData.containsAll(requiredData);
    }

    /**
     * @return The next frame of execution, can be NULL.
     */
    public PipelineFrame getNextFrame() {
        return this.nextFrame;
    }

    private void setNextFrame(final PipelineFrame nextFrame) {
        this.nextFrame = nextFrame;
    }

    /**
     * @return Unmodifiable list of all isolated metrics that are finished in this frame.
     */
    public List<IsolatedMetric> getIsolatedMetrics() {
        return Collections.unmodifiableList(this.isolatedMetrics);
    }

    /**
     * @return Unmodifiable list of all shared metrics that are finished in this frame.
     */
    public List<SharedMetric> getSharedMetrics() {
        return Collections.unmodifiableList(this.sharedMetrics);
    }

    /**
     * @return Unmodifiable list of all producers that are finished in this frame.
     */
    public List<ProducerMetric> getProducerMetrics() {
        return Collections.unmodifiableList(this.producerMetrics);
    }

    void registerMetric(final BaseMetric metric) throws MetricPreparationException {
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
