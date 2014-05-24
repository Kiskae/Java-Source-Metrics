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

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IIPUTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        this.testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.packagemetrics.iipu.", new IIPU());
    }

    @Test
    public void classEqualUses() throws ExecutionException {
        final Set<String> classes = ImmutableSet.of(
                "package1.Class1",
                "package1.Uses1", //Inside extends
                "package2.Uses2"  //Outside extends
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(classes));

        assertTrue(results.hasResults(MetricScope.COLLECTION, TestResults.COLLECTION_IDENTIFIER, IIPU.class));

        final double result = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPU.class
        );

        //UsesSum(C) = [Class1]
        //UsesSum(P) = [Class1]
        //IIPU(M) = 1 - UsesSum(P)/UsesSum(C) = 0
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void classOnlyInternal() throws ExecutionException {
        final Set<String> classes = ImmutableSet.of(
                "package1.Class1",
                "package1.Uses1" //Inside extends
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(classes));

        assertTrue(results.hasResults(MetricScope.COLLECTION, TestResults.COLLECTION_IDENTIFIER, IIPU.class));

        final double result = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPU.class
        );

        //UsesSum(C) = [Class1]
        //UsesSum(P) = []
        //IIPU(M) = 1 - UsesSum(P) / UsesSum(C) = 1
        assertEquals(1.0, result, 0.01);
    }

    @Test
    public void classNoUses() throws ExecutionException {
        final Set<String> classes = ImmutableSet.of(
                "package1.Class1"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(classes));

        assertTrue(results.hasResults(MetricScope.COLLECTION, TestResults.COLLECTION_IDENTIFIER, IIPU.class));

        final double result = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPU.class
        );

        //UsesSum(C) = []
        //UsesSum(P) = []
        //IIPU(M) = 1 - UsesSum(P) / UsesSum(C) = 1
        assertEquals(1.0, result, 0.01);
    }

    @Test
    public void classAllExternal() throws ExecutionException {
        final Set<String> classes = ImmutableSet.of(
                "package1.Class1",
                "package2.Uses2"  //Outside extends
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(classes));

        assertTrue(results.hasResults(MetricScope.COLLECTION, TestResults.COLLECTION_IDENTIFIER, IIPU.class));

        final double result = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPU.class
        );

        //UsesSum(C) = [Class1]
        //UsesSum(P) = [Class1]
        //IIPU(M) = 1 - UsesSum(P) / UsesSum(C) = 0
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void classMoreInternalUses() throws ExecutionException {
        final Set<String> classes = ImmutableSet.of(
                "package1.Class1",
                "package1.Class2",
                "package1.Uses1", //Inside extends
                "package2.Uses2"  //Outside extends
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(classes));

        assertTrue(results.hasResults(MetricScope.COLLECTION, TestResults.COLLECTION_IDENTIFIER, IIPU.class));

        final double result = results.getDoubleResult(
                MetricScope.COLLECTION,
                TestResults.COLLECTION_IDENTIFIER,
                IIPU.class
        );

        //UsesSum(C) = [Class1, Class2]
        //UsesSum(P) = [Class1]
        //IIPU(M) = 1 - UsesSum(P)/UsesSum(C) = 0.5
        assertEquals(0.5, result, 0.01);
    }
}