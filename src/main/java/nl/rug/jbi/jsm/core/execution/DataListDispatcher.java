package nl.rug.jbi.jsm.core.execution;

import nl.rug.jbi.jsm.core.event.EventBus;

import java.util.List;

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
