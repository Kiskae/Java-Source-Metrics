package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.event.EventBus;

import java.util.List;

/**
 * Runnable that delivers a set of data to a shared target.
 * The data is produced by {@link nl.rug.jbi.jsm.core.calculator.ProducerMetric}
 * Used in tandem with {@link nl.rug.jbi.jsm.core.execution.CalculationStageTask}.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
class DataListDispatcher implements Runnable {
    private final EventBus eBus;
    private final List objectList;

    public DataListDispatcher(final EventBus eBus, final List objectList) {
        this.eBus = eBus;
        this.objectList = objectList;
    }

    @Override
    public void run() {
        for (final Object obj : this.objectList) {
            this.eBus.publish(obj);
        }
    }
}
