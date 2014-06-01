package nl.rug.jbi.jsm.metrics.ckjm.rfc;

import com.google.common.base.Objects;
import org.junit.Assert;

public class ExternalOverloaded {

    public void method() {
        //Test primitive polymorphism
        Assert.assertArrayEquals(new byte[1], new byte[1]);
        Assert.assertArrayEquals(new short[1], new short[1]);
        Assert.assertArrayEquals(new int[1], new int[1]);
        Assert.assertArrayEquals(new long[1], new long[1]);

        //Test object-based polymorphism
        Objects.toStringHelper(ExternalOverloaded.class);
        Objects.toStringHelper(this);
    }
}
