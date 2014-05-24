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
public class IPSCTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.", new IPSC());
    }

    @Test
    public void testCaseA() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_a.p.C1",
                "case_a.p.C2",
                "case_a.p.C3",
                "case_a.p.C4",
                "case_a.p.C5",
                "case_a.p.C6",
                "case_a.p.C7",
                "case_a.q1.User1",
                "case_a.q2.User2",
                "case_a.q3.User3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_a.p",
                IPSC.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_a.p",
                IPSC.class
        );

        //Given by paper, IPSC(p) = 1 for Fig. 4(a)
        assertEquals(1.0, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        );

        //IPSC(case_a.p)  = 1.0
        //IPSC(case_a.q1) = 1.0
        //IPSC(case_a.q2) = 1.0
        //IPSC(case_a.q3) = 1.0
        assertEquals(1.0, collectionResult, 0.001);
    }

    @Test
    public void testCaseB() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_b.p.C1",
                "case_b.p.C2",
                "case_b.p.C3",
                "case_b.p.C4",
                "case_b.p.C5",
                "case_b.p.C6",
                "case_b.p.C7",
                "case_b.q1.User1",
                "case_b.q2.User2",
                "case_b.q3.User3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_b.p",
                IPSC.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_b.p",
                IPSC.class
        );

        //Given by paper, IPSC(p) = 14/15 for Fig. 4(b)
        assertEquals(14.0 / 15, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        );

        //IPSC(case_a.p)  = 14 / 15
        //IPSC(case_a.q1) = 1.0
        //IPSC(case_a.q2) = 1.0
        //IPSC(case_a.q3) = 1.0
        assertEquals((14.0 / 15 + 3) / 4, collectionResult, 0.001);
    }

    @Test
    public void testCaseC() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_c.p.C1",
                "case_c.p.C2",
                "case_c.p.C3",
                "case_c.p.C4",
                "case_c.p.C5",
                "case_c.p.C6",
                "case_c.p.C7",
                "case_c.q1.User1",
                "case_c.q2.User2",
                "case_c.q3.User3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_c.p",
                IPSC.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipsc.case_c.p",
                IPSC.class
        );

        //The value given by paper, IPSC(p) = 32/45 for Fig. 4(c), is wrong.
        //CS cohesion(p, p1) = (8 / 5) / 3 = 8 / 15 => * 54  = 432 / 810
        //CS cohesion(p, q2) = (5 / 2) / 3 = 5 / 6  => * 135 = 675 / 810
        //CS cohesion(p, q3) = (5 / 3) / 3 = 5 / 9  => * 90  = 450 / 810
        //IPSC(p) = ((432 + 675 + 450) / 3) / 810 = 519 / 810 = 173 / 270
        assertEquals(173.0 / 270, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPSC.class
        );

        //IPSC(case_a.p)  = 173 / 270
        //IPSC(case_a.q1) = 1.0
        //IPSC(case_a.q2) = 1.0
        //IPSC(case_a.q3) = 1.0
        assertEquals((173.0 / 270 + 3) / 4, collectionResult, 0.001);
    }
}