package nl.rug.jbi.jsm.core.event;

public class MetricExecutionException extends Exception {
    public MetricExecutionException(final String msg, final Throwable e) {
        super(msg, e);
    }
}
