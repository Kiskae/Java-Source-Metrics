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
public class IIPEDTest {
    private final static String targetPackage = "nl.rug.jbi.jsm.metrics.packagemetrics.iiped.target";
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.iiped.", new IIPED());
    }

    @Test
    public void test10Classes() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "target.Class1",
                "ext1.Extend1",
                "ext2.Extend2",
                "ext3.Extend3",
                "ext4.Extend4",
                "ext5.Extend5",
                "ext6.Extend6",
                "ext7.Extend7",
                "ext8.Extend8",
                "ext9.Extend9",
                "ext10.Extend10"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        );

        //Ext(p) = 10
        //ExtC(p) = 10
        //IIPED(p) = (1/10) * (1 - (9/10)) = 1/100

        assertEquals(1.0 / 100, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        );

        //IIPED(extX) = 1 (Because ExtC(extX) = 0)
        //IIPED(M) = ((1/100) + 10) / 11
        assertEquals((1.0 / 100 + 10) / 11, collectionResult, 0.001);
    }

    @Test
    public void test5Classes() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "target.Class2",
                "ext1.Extend1",
                "ext2.Extend2",
                "ext3.Extend3",
                "ext4.Extend4",
                "ext5.Extend5"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        );

        //Ext(p) = 5
        //ExtC(p) = 5
        //IIPED(p) = (1/5) * (1 - (4/5)) = 1/25

        assertEquals(1.0 / 25, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        );

        //IIPED(extX) = 1 (Because ExtC(extX) = 0)
        //IIPED(M) = ((1/25) + 5) / 6
        assertEquals((1.0 / 25 + 5) / 6, collectionResult, 0.001);
    }

    @Test
    public void test0Classes() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "target.Class3"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        ));

        final double result = results.getDoubleResult(
                MetricScope.PACKAGE,
                targetPackage,
                IIPED.class
        );

        //Ext(p) = 0
        //ExtC(p) = 0
        //IIPED(p) = 1 since ExtC(p) = 0

        assertEquals(1.0, result, 0.001);

        //Collections
        assertTrue(results.hasResults(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        ));

        final double collectionResult = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPED.class
        );

        //IIPED(extX) = 1 (Because ExtC(extX) = 0)
        //IIPED(M) = (1 + 5) / 6
        assertEquals(1.0, collectionResult, 0.001);
    }
}