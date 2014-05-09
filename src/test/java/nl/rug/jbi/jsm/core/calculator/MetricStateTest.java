package nl.rug.jbi.jsm.core.calculator;

import nl.rug.jbi.jsm.util.DefaultValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class MetricStateTest {
    private final static DefaultValue<Integer> ZERO = new DefaultValue<Integer>() {
        @Override
        public Integer getDefault() {
            return 0;
        }
    };

    private MetricState ms;

    @Before
    public void setUp() throws Exception {
        this.ms = new MetricState("java.lang.Object", MetricStateTest.class);
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals("should be null", this.ms.getValue("test"), null);
        this.ms.setValue("test", 1);
        assertEquals("should be 1", this.ms.getValue("test"), 1);
    }

    @Test
    public void testGetValueDefault() throws Exception {
        assertEquals("should be 0", this.ms.getValue("test", ZERO), Integer.valueOf(0));
        this.ms.setValue("test", 1);
        assertEquals("should be 1", this.ms.getValue("test", ZERO), Integer.valueOf(1));
    }

    @Test
    public void testSetValue() throws Exception {
        this.ms.setValue("test", 1);
        assertEquals("should be 1", this.ms.getValue("test"), 1);
        this.ms.setValue("test", 2);
        assertEquals("should be 2", this.ms.getValue("test"), 2);
    }

    @Test(expected = NullPointerException.class)
    public void testSetNull() throws Exception {
        this.ms.setValue("test", null);
    }

    @Test
    public void testDeleteValue() throws Exception {
        this.ms.setValue("test", 1);
        assertEquals("should be 1", this.ms.getValue("test", ZERO), Integer.valueOf(1));
        assertTrue("should be true", this.ms.deleteValue("test"));
        assertEquals("should be 0", this.ms.getValue("test", ZERO), Integer.valueOf(0));
    }
}