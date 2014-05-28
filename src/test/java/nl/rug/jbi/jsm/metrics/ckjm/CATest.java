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
public class CATest {
    private TestFrontend testFrontend = null;

    private static void checkClass(String suffix, TestResults results) {
        final String className = "nl.rug.jbi.jsm.metrics.ckjm.ca.RefTypes" + suffix;
        assertTrue(results.hasResults(MetricScope.CLASS, className, CA.class));
        assertEquals(
                1.0,
                results.getDoubleResult(MetricScope.CLASS, className, CA.class),
                0.001
        );
    }

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.ca.", new CA());
    }

    @Test
    public void testRefAll() throws ExecutionException {
        //Since CBOTest already tests the reference tracking, this test just tests the reverse-mapping.
        final Set<String> targetClasses = ImmutableSet.of(
                "RefAll",
                "RefTypes",
                "RefTypes$Map",
                "RefTypes$List",
                "RefTypes$FakeInterface",
                "RefTypes$Exception"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.ca.RefAll", CA.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.ca.RefAll",
                CA.class
        );

        //Nothing actually refers to RefAll
        assertEquals(0.0, result, 0.001);

        checkClass("", results); //RefTypes
        checkClass("$Map", results); //RefTypes.Map
        checkClass("$List", results); //RefTypes.List
        checkClass("$FakeInterface", results); //RefTypes.FakeInterface
        checkClass("$Exception", results); //RefTypes.Exception
    }
}