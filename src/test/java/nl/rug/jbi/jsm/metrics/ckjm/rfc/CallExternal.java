package nl.rug.jbi.jsm.metrics.ckjm.rfc;

import com.google.common.base.Objects;

public class CallExternal {

    public void method() {
        String.valueOf(this);
        Objects.equal(this, this);
        Objects.hashCode(this);
    }
}
