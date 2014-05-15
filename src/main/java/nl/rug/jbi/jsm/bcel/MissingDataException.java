package nl.rug.jbi.jsm.bcel;

public class MissingDataException extends RuntimeException {

    public MissingDataException(final String msg, final Throwable thr) {
        super(msg, thr);
    }
}
