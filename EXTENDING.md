Extending JSM
===============

JSM can be extended in two ways:
* Custom metrics, using the built-in data to calculate custom metrics and report the results.
* Modified class visitor, expand the set of data available within the default frame.

Custom Metrics
--------------

JSM uses a system of 3 metric-types that work to create a directional graph of dependencies to calculate their results.

* The first metric-type is the [IsolatedMetric](src/main/java/nl/rug/jbi/jsm/core/calculator/IsolatedMetric.java). This type is meant for metrics that can be calculated with just the information provided by the source. (The source being class/package/collection-specific sets of data)
* The second metric-type is the [SharedMetric](src/main/java/nl/rug/jbi/jsm/core/calculator/SharedMetric.java). The shared metric is a metric where information from sources other than the assigned source are required, an example being a metric that requires reverse-mapping of relations. These metrics recieve the full dataset from all individual sources and process that dataset into a set of results.
* The final metric-type is the [ProducerMetric](src/main/java/nl/rug/jbi/jsm/core/calculator/ProducerMetric.java). This type serves to both act as a common data-source for a set of metrics as well as bridge data between scopes. Producers expand the available data-set that is available to Isolated and Shared Metrics.

The actual definition of each metric consists of two parts. First of all the metric needs to define a set of listeners with the signature:
```java
@Subscribe
public void <name>(MetricState state, <ObjectType> obj) {
  ...
}
```
The [Subscribe](src/main/java/nl/rug/jbi/jsm/core/event/Subscribe.java) annotation marks the method as a listener method, when the system encounters an object of type `<ObjectType>`, it will invoke that method with a system-managed State object. A list of objects that are available in the system by default can be found in [nl.rug.jbi.jsm.bcel](src/main/java/nl/rug/jbi/jsm/bcel).

A method can also listen for a Producer-produced Object, this is done with the following signature:
```java
@Subscribe
@UsingProducer(<ProducerClass>.class)
public void <name>(MetricState state, <Produce> obj) {
  ...
}
```

The second part of the metric definition is the results/produce calculation, this is implemented as an abstract method in each of the metric-type classes. Shared/Producer-Metrics in general shouldn't return null, but an empty List if there are no results. Please refer to the javadocs for more information. Example implementations of metrics can be found in the [nl.rug.jbi.jsm.metrics](src/main/java/nl/rug/jbi/jsm/metrics) package.

If a metric returns results of a different scope than its execution or for multiple scopes, the method *EnumSet<MetricScope> BaseMetric#getResultScopes()* should be overridden to return the set specifying those scopes.

Modifying the Class Visitor
-----------------

Using a modified class visitor is a three-part process:

1. Create a subclass of the default *ClassVisitor*, if methods are overridden to add more data to broadcast, the super-method needs to be called to ensure the class-visitor completes. Objects can be broadcast using the [EventBus](src/main/java/nl/rug/jbi/jsm/core/event/EventBus.java) object that is available in the class visitor.
2. Create an implementation of the *ClassVisitorFactory*. First create a method to create and execute the custom class visitor for the given *JavaClass*. Then implement the *getDefaultDataClasses* method to return a set of all data-classes exposed by the class visitor implementation. If the implementation extends the default class visitor, it should be a superset of the *ClassVisitor#DEFAULT_CLASSES* set.
