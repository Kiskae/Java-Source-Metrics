package nl.rug.jbi.jsm.metrics.ckjm.cbo;


public class RefExceptionHandler {

    public void method() {
        try {
            //If try-block is empty, the entire structure gets ignored.
            int a = 1;
        } catch (RefTypes.Exception ex) {
            ex.printStackTrace();
        }
    }
}
