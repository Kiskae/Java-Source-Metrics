package nl.rug.jbi.jsm.core;

import nl.rug.jbi.jsm.core.calculator.*;
import nl.rug.jbi.jsm.core.event.Subscribe;
import nl.rug.jbi.jsm.core.event.UsingProducer;
import nl.rug.jbi.jsm.core.pipeline.MetricPreparationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class MetricValidationTest {
    private JSMCore core = null;

    @Before
    public void setUp() throws Exception {
        this.core = new JSMCore();
    }

    @Test(expected = MetricPreparationException.class)
    public void testProducerDirectRegistration() throws MetricPreparationException {
        //Direct registration of a producer is forbidden.
        this.core.registerMetric(new ProducerMetric(MetricScope.CLASS, MetricScope.CLASS) {
            @Override
            public List<Produce> getProduce(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Class getProducedClass() {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testIllegalScopeOrder() throws MetricPreparationException {
        //Results can either be of the execution scope or a future scope, but not a previous scope.
        this.core.registerMetric(new SharedMetric(MetricScope.COLLECTION) {
            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }

            @Override
            public EnumSet<MetricScope> getResultScopes() {
                //Class gets evaluated before collection, so collection cannot be produced by class.
                return EnumSet.of(MetricScope.CLASS);
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testMissingData() throws MetricPreparationException {
        //Tests case where a class needs data that is neither provided by the class visitor, nor a producer.
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            public void handleData(final MetricState state, final Object randomObj) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testInvalidListenerArgCount() throws MetricPreparationException {
        //A Subscribed method needs to have 2 parameters
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            public void handleData(final MetricState state) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testWrongFirstParameter() throws MetricPreparationException {
        //The first parameter needs to be MetricState
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            public void handleData(final Object randomObj, final MetricState state) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testListenerPrivate() throws MetricPreparationException {
        //Listener methods need to be public
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            void handleData(final Object randomObj, final MetricState state) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testProducerProduceMismatch() throws MetricPreparationException {
        //If a producer is declared, the second parameter needs to match the produce of the producer
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            @UsingProducer(TestProducer.class)
            public void handleData(final MetricState state, final Integer one) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testProducerScopeMistmatch() throws MetricPreparationException {
        //If a producer is declared, the produce scope needs to match the metric scope.
        this.core.registerMetric(new SharedMetric(MetricScope.COLLECTION) {
            @Subscribe
            @UsingProducer(TestProducer.class)
            public void handleData(final MetricState state, final Object one) {
                //The metric calculates for COLLECTION, but the producer is for CLASS
            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testCyclicProducers() throws MetricPreparationException {
        //CyclicProducer1 includes CyclicProducer2, which include CyclicProducer1 again
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            @UsingProducer(CyclicProducer1.class)
            public void handleData(final MetricState state, final Integer one) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test(expected = MetricPreparationException.class)
    public void testUsingSelf() throws MetricPreparationException {
        //Producer using itself, creates implicit cyclic requirement.
        this.core.registerMetric(new SharedMetric(MetricScope.CLASS) {
            @Subscribe
            @UsingProducer(CyclicSelf.class)
            public void handleData(final MetricState state, final Integer one) {

            }

            @Override
            public List<MetricResult> getResults(Map<String, MetricState> states, int invalidMembers) {
                throw new UnsupportedOperationException();
            }
        });
    }

    public static class TestProducer extends ProducerMetric {

        public TestProducer() {
            super(MetricScope.CLASS, MetricScope.CLASS);
        }

        @Override
        public List<Produce> getProduce(Map<String, MetricState> states, int invalidMembers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class getProducedClass() {
            return Object.class;
        }
    }

    public static class CyclicProducer1 extends ProducerMetric {

        public CyclicProducer1() {
            super(MetricScope.CLASS, MetricScope.CLASS);
        }

        @Subscribe
        @UsingProducer(CyclicProducer2.class)
        public void noOp(final MetricState state, final Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Produce> getProduce(Map<String, MetricState> states, int invalidMembers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class getProducedClass() {
            return Object.class;
        }
    }

    public static class CyclicProducer2 extends ProducerMetric {

        public CyclicProducer2() {
            super(MetricScope.CLASS, MetricScope.CLASS);
        }

        @Subscribe
        @UsingProducer(CyclicProducer1.class)
        public void noOp(final MetricState state, final Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Produce> getProduce(Map<String, MetricState> states, int invalidMembers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class getProducedClass() {
            return Object.class;
        }
    }

    public static class CyclicSelf extends ProducerMetric {

        public CyclicSelf() {
            super(MetricScope.CLASS, MetricScope.CLASS);
        }

        @Subscribe
        @UsingProducer(CyclicSelf.class)
        public void noOp(final MetricState state, final Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Produce> getProduce(Map<String, MetricState> states, int invalidMembers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class getProducedClass() {
            return Object.class;
        }
    }
}
