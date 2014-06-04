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
public class RFCTest {
    private TestFrontend testFrontend = null;

    @Before
    public void setUp() throws Exception {
        testFrontend = new TestFrontend("nl.rug.jbi.jsm.metrics.ckjm.rfc.", new RFC());
    }

    @Test
    public void testInternalMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "InternalMethodsOnly"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.rfc.InternalMethodsOnly", RFC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.rfc.InternalMethodsOnly",
                RFC.class
        );

        //InternalMethodsOnly.<init>()
        //Object.<init>() - super()
        //InternalMethodsOnly.method1()
        //InternalMethodsOnly.method2()
        assertEquals(4.0, result, 0.001);
    }

    @Test
    public void testExternalMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "CallExternal"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.rfc.CallExternal", RFC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.rfc.CallExternal",
                RFC.class
        );

        //CallExternal.<init>()
        //Object.<init>() - super()
        //CallExternal.method()
        //String.valueOf(Object)
        //com.google.common.base.Objects.hashCode(Object[])
        //com.google.common.base.Objects.equal(Object, Object)
        assertEquals(6.0, result, 0.001);
    }

    @Test
    public void testInternalOverloadedMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "InternalOverloaded"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.rfc.InternalOverloaded", RFC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.rfc.InternalOverloaded",
                RFC.class
        );

        //InternalMethodsOnly.<init>()
        //Object.<init>() - super()
        //InternalMethodsOnly.method()
        //InternalMethodsOnly.method(Object)
        //InternalMethodsOnly.method(Object,Object)
        assertEquals(5.0, result, 0.001);
    }

    @Test
    public void testExternalOverloadedMethods() throws ExecutionException {
        final Set<String> targetClasses = ImmutableSet.of(
                "ExternalOverloaded"
        );

        final TestResults results = Uninterruptibles.getUninterruptibly(this.testFrontend.executeTest(targetClasses));

        assertTrue(results.hasResults(MetricScope.CLASS, "nl.rug.jbi.jsm.metrics.ckjm.rfc.ExternalOverloaded", RFC.class));

        final double result = results.getDoubleResult(
                MetricScope.CLASS,
                "nl.rug.jbi.jsm.metrics.ckjm.rfc.ExternalOverloaded",
                RFC.class
        );

        //ExternalOverloaded.<init>()
        //Object.<init>() - super()
        //ExternalOverloaded.method()
        //Assert.assertArrayEquals(byte[],byte[]);
        //Assert.assertArrayEquals(short[],short[]);
        //Assert.assertArrayEquals(int[],int[]);
        //Assert.assertArrayEquals(long[],long[]);
        //Objects.toStringHelper(Object)
        //Objects.toStringHelper(Class)
        assertEquals(9.0, result, 0.001);
    }
}