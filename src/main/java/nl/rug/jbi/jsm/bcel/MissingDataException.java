package nl.rug.jbi.jsm.bcel;

/**
 * Exception thrown by the classes in this package if the underlying BCEL structure throws a
 * {@link java.lang.ClassNotFoundException}. This exception will be caught by the executor and will invalidate the
 * invoked metric.
 *
 * @author David van Leusen
 * @since 1.0
 */
public class MissingDataException extends RuntimeException {

    public MissingDataException(final String msg, final Throwable thr) {
        super(msg, thr);
    }
}
