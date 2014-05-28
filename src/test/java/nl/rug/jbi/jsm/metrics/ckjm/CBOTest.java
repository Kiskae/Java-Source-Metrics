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
public class CBOTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.cbo.", new CBO());
    }

    @Test
    public void testRefMethod() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefMethod"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefMethod", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefMethod",
                CBO.class
        );

        //Referenced Types:
        //Return = nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Args = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        //Exception = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Exception]
        assertEquals(3.0, result, 0.001);
    }

    @Test
    public void testRefExceptionHandler() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefExceptionHandler"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefExceptionHandler", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefExceptionHandler",
                CBO.class
        );

        //Referenced Types:
        //Exceptions caught = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Exception]
        assertEquals(1.0, result, 0.001);
    }

    @Test
    public void testRefField() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefField"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefField", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefField",
                CBO.class
        );

        //Referenced Types:
        //Field types = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testRefFieldAccess() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefFieldAccess"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefFieldAccess", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefFieldAccess",
                CBO.class
        );

        //Referenced Types:
        //Accessed Type: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes]
        //Field Type: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map]
        //java.util.Objects access is ignored!
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testRefLocalVar() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefLocalVar"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefLocalVar", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefLocalVar",
                CBO.class
        );

        //Referenced Types:
        //Local Vars: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testRefClass() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefClass"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefClass", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefClass",
                CBO.class
        );

        //Referenced Types:
        //Extends: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Implements: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testRefTypeUse() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefTypeUse"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypeUse", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypeUse",
                CBO.class
        );

        //Referenced Types:
        //INSTANCEOF: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List
        //CAST: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testRefMethodInvocation() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefMethodInvocation"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefMethodInvocation", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefMethodInvocation",
                CBO.class
        );

        //Referenced Types:
        //invoked class: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes
        //Return: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Args: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface]
        assertEquals(4.0, result, 0.001);
    }

    @Test
    public void testRefAll() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "RefAll"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefAll", CBO.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.cbo.RefAll",
                CBO.class
        );

        //Referenced Types:
        //Return = nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Args = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        //Exception = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Exception]
        //Exceptions caught = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Exception]
        //invoked class: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes
        //Return: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Args: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface]
        //Field types = [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        //Accessed Type: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes]
        //Field Type: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map]
        //Local Vars: [nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map, nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List]
        //Extends: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.Map
        //Implements: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface
        //INSTANCEOF: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.List
        //CAST: nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes.FakeInterface

        //Basically all types created by nl.rug.jbi.jsm.metrics.ckjm.cbo.RefTypes
        assertEquals(5.0, result, 0.001);
    }
}