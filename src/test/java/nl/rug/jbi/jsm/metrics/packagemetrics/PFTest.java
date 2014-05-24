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
public class PFTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.pf.", new PF());
    }

    @Test
    public void testCaseA() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_a.p.Target1",
                "case_a.p.Target2",
                "case_a.p.Target3",
                "case_a.p.Target4",
                "case_a.p.Target5",
                "case_a.p1.User",
                "case_a.p2.User",
                "case_a.p3.User",
                "case_a.p4.User"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_a.p",
                PF.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_a.p",
                PF.class
        );

        assertEquals(1.0, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        );

        //PF(case_a.p)  = 1.0
        //PF(case_a.p1) = 1.0
        //PF(case_a.p2) = 1.0
        //PF(case_a.p3) = 1.0
        //PF(case_a.p4) = 1.0
        assertEquals(1.0, collectionResult, 0.001);
    }

    @Test
    public void testCaseB() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_b.q.Target1",
                "case_b.q.Target2",
                "case_b.q.Target3",
                "case_b.q.Target4",
                "case_b.q.Target5",
                "case_b.q1.User",
                "case_b.q2.User",
                "case_b.q3.User",
                "case_b.q4.User"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_b.q",
                PF.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_b.q",
                PF.class
        );

        assertEquals(2.0 / 5, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        );

        //PF(case_b.q)  = 2 / 5
        //PF(case_b.q1) = 1.0
        //PF(case_b.q2) = 1.0
        //PF(case_b.q3) = 1.0
        //PF(case_b.q4) = 1.0
        assertEquals((2.0 / 5 + 4) / 5, collectionResult, 0.001);
    }

    @Test
    public void testCaseC() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_c.k.Target1",
                "case_c.k.Target2",
                "case_c.k.Target3",
                "case_c.k.Target4",
                "case_c.k.Target5",
                "case_c.k1.User",
                "case_c.k2.User",
                "case_c.k3.User",
                "case_c.k4.User"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_c.k",
                PF.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_c.k",
                PF.class
        );

        assertEquals(1.0 / 2, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        );

        //PF(case_c.k)  = 1 / 2
        //PF(case_c.k1) = 1.0
        //PF(case_c.k2) = 1.0
        //PF(case_c.k3) = 1.0
        //PF(case_c.k4) = 1.0
        assertEquals((1.0 / 2 + 4) / 5, collectionResult, 0.001);
    }

    @Test
    public void testCaseD() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_d.y.Target1",
                "case_d.y.Target2",
                "case_d.y.Target3",
                "case_d.y.Target4",
                "case_d.y.Target5",
                "case_d.y1.User",
                "case_d.y2.User",
                "case_d.y3.User",
                "case_d.y4.User"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_d.y",
                PF.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.pf.case_d.y",
                PF.class
        );

        assertEquals(11.0 / 20, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                PF.class
        );

        //PF(case_d.y)  = 11 / 20
        //PF(case_d.y1) = 1.0
        //PF(case_d.y2) = 1.0
        //PF(case_d.y3) = 1.0
        //PF(case_d.y4) = 1.0
        assertEquals((11.0 / 20 + 4) / 5, collectionResult, 0.001);
    }
}