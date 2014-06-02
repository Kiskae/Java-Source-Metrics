package nl.rug.jbi.jsm.core.pipeline;

/**
 * Exception thrown is there is an issue with the definition or registration of a metric.
 * The error message will always contain the reason for the exception.
 *
 * @author David van Leusen
 * @since 2014-06-02
 */
public class MetricPreparationException extends Exception {
    public MetricPreparationException(final String msg) {
        super(msg);
    }

    public MetricPreparationException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
