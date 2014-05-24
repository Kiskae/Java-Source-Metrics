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
public class IPCITest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.ipci.", new IPCI());
    }

    @Test
    public void testCaseA() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_a.p.Target1",
                "case_a.p.Target2",
                "case_a.p.Target3",
                "case_a.p.Target4",
                "case_a.p.Target5",
                "case_a.p.Target6",
                "case_a.p.Target7",
                "case_a.p1.User"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_a.p",
                IPCI.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_a.p",
                IPCI.class
        );

        //ClientsP(case_a.p) = 1
        //len(P) = 2
        //IPCI(case_a.p) = 1 - (1/1) = 0

        assertEquals(0.0, result, 0.001);

        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        );

        //IPCI(case_a.p) = 0
        //IPCI(case_a.p1) = 1
        //len(P) = 2
        //IPCI(M) = (0 + 1) / 2

        assertEquals(0.5, collectionResult, 0.001);
    }

    @Test
    public void testCaseB() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_b.q.Target",
                "case_b.q1.User1",
                "case_b.q2.User2",
                "case_b.q3.User3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_b.q",
                IPCI.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_b.q",
                IPCI.class
        );

        //ClientsP(case_b.q) = 3
        //len(P) = 4
        //IPCI(case_b.q) = 1 - (3/3) = 0

        assertEquals(0.0, result, 0.001);

        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        );

        //IPCI(case_b.q) = 0
        //IPCI(case_b.q1) = 1
        //IPCI(case_b.q2) = 1
        //IPCI(case_b.q3) = 1
        //len(P) = 4
        //IPCI(M) = (0 + 1 + 1 + 1) / 4

        assertEquals(0.75, collectionResult, 0.001);
    }

    @Test
    public void testCaseC() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "case_c.q.Target",
                "case_c.q1.User1",
                "case_c.q2.User2",
                "case_c.q3.User3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_c.q",
                IPCI.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                "nl.rug.jbi.jsm.metrics.packagemetrics.ipci.case_c.q",
                IPCI.class
        );

        //ClientsP(case_c.q) = 2
        //len(P) = 4
        //IPCI(case_c.q) = 1 - (2/3) = 1/3

        assertEquals(1.0 / 3, result, 0.001);

        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IPCI.class
        );

        //IPCI(case_c.q) = 1 / 3
        //IPCI(case_c.q1) = 1
        //IPCI(case_c.q2) = 1
        //IPCI(case_c.q3) = 1
        //len(P) = 4
        //IPCI(M) = (1 / 3 + 1 + 1 + 1) / 4 = 33 / 40

        assertEquals(33.0 / 40, collectionResult, 0.01);
    }
}