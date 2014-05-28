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
public class DITTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.dit.", new DIT());
    }

    @Test
    public void testRootObject() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RootObject"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.dit.RootObject", DIT.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.dit.RootObject",
                DIT.class
        );

        //RootObject's inheritance tree is just java.lang.Object
        assertEquals(1.0, result, 0.001);
    }

    @Test
    public void testExtendingObject() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "ExtendingObject"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.dit.ExtendingObject", DIT.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.dit.ExtendingObject",
                DIT.class
        );


        //ExtendingObject's inheritance tree is RootObject -> java.lang.Object
        assertEquals(2.0, result, 0.001);
    }
}