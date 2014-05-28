package nl.rug.jbi.jsm.metrics.ckjm.rfc;

import java.util.Objects;

public class CallExternal {

    public void method() {
        Objects.toString(this);
        Objects.hash(this);
        Objects.equals(this, this);
    }
}
