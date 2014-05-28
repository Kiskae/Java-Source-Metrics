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
public class LCOMTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.lcom.", new LCOM());
    }

    @Test
    public void testAllUseFields() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "AllUseFields"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.lcom.AllUseFields", LCOM.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.lcom.AllUseFields",
                LCOM.class
        );

        //All methods use all fields, so lcom = 0
        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testDisjunctFields() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "DisjunctFields"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.lcom.DisjunctFields", LCOM.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.lcom.DisjunctFields",
                LCOM.class
        );

        //All methods use different fields, so:
        //<init> + method1
        //<init> + method2
        //method1 + method2
        //Result == 3
        assertEquals(3.0, result, 0.001);
    }

    @Test
    public void testNoFields() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "NoFields"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.lcom.NoFields", LCOM.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.lcom.NoFields",
                LCOM.class
        );

        //There are no fields, so no methods can share fields.
        //<init> + method1
        //<init> + method2
        //<init> + method3
        //method1 + method2
        //method1 + method3
        //method2 + method3
        //Result == 6
        assertEquals(6.0, result, 0.001);
    }

    @Test
    public void testNoMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "NoMethods"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.lcom.NoMethods", LCOM.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.lcom.NoMethods",
                LCOM.class
        );

        //If there are no methods, the only method is the initializer
        //It cannot share fields with something that doesn't exist, so it'll always be 0
        assertEquals(0.0, result, 0.001);
    }
}