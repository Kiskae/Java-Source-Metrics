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
public class NOCTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.noc.", new NOC());
    }

    @Test
    public void testNoChildren() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "NoChildren"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.noc.NoChildren", NOC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.noc.NoChildren",
                NOC.class
        );

        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testChildren() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "Parent",
                "ChildA",
                "ChildB"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.noc.Parent", NOC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.noc.Parent",
                NOC.class
        );

        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testChildrenPartial() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "Parent",
                "ChildA"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.noc.Parent", NOC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.noc.Parent",
                NOC.class
        );

        //Since only 1 child is inspected, Parent will have a NOC of 1
        assertEquals(1.0, result, 0.001);
    }
}