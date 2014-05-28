package nl.rug.jbi.jsm.metrics.ckjm.rfc;

import org.junit.Assert;

public class ExternalOverloaded {

    public void method() {
        Assert.assertArrayEquals(new byte[1], new byte[1]);
        Assert.assertArrayEquals(new short[1], new short[1]);
        Assert.assertArrayEquals(new int[1], new int[1]);
        Assert.assertArrayEquals(new long[1], new long[1]);
    }
}
