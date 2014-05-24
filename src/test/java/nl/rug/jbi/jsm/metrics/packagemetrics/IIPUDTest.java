package nl.rug.jbi.jsm.metrics.packagemetrics;

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
public class IIPUDTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.iipud.", new IIPUD());
    }

    @Test
    public void testCaseA() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_a.p.User",
                "case_a.p1.Target1",
                "case_a.p1.Target2",
                "case_a.p1.Target3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p",
                IIPUD.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_a.p",
                IIPUD.class
        );

        //Uses(p) = 1
        //UsesC(p) = 3
        //IIPUD(p) = (1 / 1) * (1 - ((1 - 1) / 3)) = 1
        assertEquals(1.0, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        );

        //IIPUD(case_a.p)  = 1.0
        //IIPUD(case_a.p1) = 1.0
        assertEquals(1.0, collectionResult, 0.001);
    }

    @Test
    public void testCaseB() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_b.q.User",
                "case_b.q1.Target1",
                "case_b.q1.Target2",
                "case_b.q2.Target3",
                "case_b.q3.Target4"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q",
                IIPUD.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_b.q",
                IIPUD.class
        );

        //Uses(p) = 3
        //UsesC(p) = 4
        //IIPUD(p) = (1 / 3) * (1 - ((3 - 1) / 4)) = (1 / 3) * (1 - (2 / 4)) = (1 / 3) * (1 / 2)
        assertEquals(1.0 / 6, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        );

        //IIPUD(case_b.q)  = 1 / 6
        //IIPUD(case_b.q1) = 1.0
        //IIPUD(case_b.q2) = 1.0
        //IIPUD(case_b.q3) = 1.0
        assertEquals((1.0 / 6 + 3) / 4, collectionResult, 0.001);
    }

    @Test
    public void testCaseC() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_c.k.User",
                "case_c.k1.Target1",
                "case_c.k1.Target2",
                "case_c.k2.Target3",
                "case_c.k3.Target4",
                "case_c.k3.Target5",
                "case_c.k4.Target6",
                "case_c.k5.Target7",
                "case_c.k5.Target8"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_c.k",
                IIPUD.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.iipud.case_c.k",
                IIPUD.class
        );

        //Uses(p) = 5
        //UsesC(p) = 8
        //IIPUD(p) = (1 / 5) * (1 - ((5 - 1) / 8)) = (1 / 5) * (1 - (4 / 8)) = (1 / 5) * (1 / 2)
        assertEquals(1.0 / 10, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPUD.class
        );

        //IIPUD(case_c.k)  = 1 / 10
        //IIPUD(case_c.k1) = 1.0
        //IIPUD(case_c.k2) = 1.0
        //IIPUD(case_c.k3) = 1.0
        //IIPUD(case_c.k4) = 1.0
        //IIPUD(case_c.k5) = 1.0
        assertEquals((1.0 / 10 + 5) / 6, collectionResult, 0.001);
    }
}