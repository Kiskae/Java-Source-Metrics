package nl.rug.jbi.jsm.metrics.ckjm;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Uninterruptibles;
import nl.rug.jbi.jsm.core.calculator.MetricScope;
import nl.rug.jbi.jsm.metrics.TestFrontend;
import nl.rug.jbi.jsm.metrics.TestResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class WMCTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.wmc.", new WMC());
    }

    @Test
    public void testAllPublic() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "AllPublic"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.wmc.AllPublic", WMC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.wmc.AllPublic",
                WMC.class
        );

        //public <init>()
        //public method1()
        //public method2()
        //public method3()
        assertEquals(4.0, result, 0.001);
    }

    @Test
    public void testNotAllPublic() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "NotAllPublic"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.wmc.NotAllPublic", WMC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.wmc.NotAllPublic",
                WMC.class
        );

        //private <init>()
        //public method1()
        //public method2()
        //private method3()
        assertEquals(4.0, result, 0.001);
    }

    @Test
    public void testNoMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "NoMethods"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.wmc.NoMethods", WMC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.wmc.NoMethods",
                WMC.class
        );

        //public <init>()
        assertEquals(1.0, result, 0.001);
    }
}