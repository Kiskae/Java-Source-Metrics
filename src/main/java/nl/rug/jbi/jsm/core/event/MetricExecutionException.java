package nl.rug.jbi.jsm.core.event;


import com.google.common.base.Objects;

public class MetricExecutionException extends Exception {
    public MetricExecutionException(final String msg, final Throwable e) {
        super(msg, e);
    }

    public String toString() {
        return Objects.toStringHelper(this)
                .add("exception-type", getCause().getClass().getName())
                .add("exception-message", getCause().getMessage())
                .toString();
    }
}
