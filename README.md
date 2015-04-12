# Java-Reactive

A simple reactive programming API for Java 8, based on Li Haoyi's [Scala.Rx](https://github.com/lihaoyi/scala.rx) implementation.

# API

In a nutshell, this tool is an extension of the observer design pattern. Programs are written using "reactive" elements - elements which react immediately when the elements they depend on change. Elements are (currently) either variables or tasks. A variable has its value defined by a supplier function, which makes use of Java 8 lambdas:

```java
RxVar<Double> a = Rx.var(1.0);
RxVar<Double> b = Rx.var(2.0);
RxVar<Double> c = Rx.var(() -> {
  return a.val() + b.val();
});

assertEquals(c.val(), 3.0);

a.set(3.0);

assertEquals(c.val(), 5.0);
```

A task is essentially the same as a variable, but it instead holds no value:

```java
RxVar<Double> a = Rx.var(1.0);

Rx.task(() -> {
  System.out.println( "'a' changed! Here is its new value: " + a.val() );
});

// Some code which makes changes to a's value...
```

# Reactive Propagation

Reactive updates are processed in a "simultaneous" fashion. Every time a var.set() method is called, all downstream variables have their values recalculated eagerly. Downstream variables have their values recalcualted in topological order, and each variable only has its value recalculated once (diamond-shaped dependencies are well-behaved):

```java
RxVar<Boolean> a = Rx.var(true);
RxVar<Boolean> b = Rx.var(() -> {
  return !a.val();
});
RxVar<Boolean> c = Rx.var(() -> {
  return !a.val();
});
RxVar<Boolean> d = Rx.var(() -> {
  return b.val() == c.val();
});

Rx.task(() -> {
  System.out.println( "d's value is: " + d.val() );
});

a.set(false);
// d's value is: true
a.set(true);
// d's value is: true
```

Performing the same task with the traditional observer pattern is certainly more difficult!

# Simultaneous Operations

Occasionally, one might wish to set several variable values before propogating the changes upstream:

```java
RxVar<Double> a = Rx.var(0.0);
RxVar<Double> b = Rx.var(0.0);
RxVar<Double> c = Rx.var(0.0);
RxVar<Double> sum = Rx.var(() -> {
  // You can do some useful work in here too, beyond just recalculating the value
  double s = a.val() + b.val() + c.val();
  System.out.println("The sum is " + s);
  return s;
});

a.set(1.0);
// The sum is 1.0
b.set(2.0);
// The sum is 3.0
c.set(3.0);
// The sum is 6.0

// Now let's set the values simultaneously, with only one print occurring:
Rx.doSync(() -> {
  a.set(4.0);
  b.set(5.0);
  c.set(6.0);
});
// The sum is 15.0
```

# Multi-threading

Complex dependency graphs often have updates which may be performed simultaneously by separate threads. This is especially useful when reactive updates are computationally intense. See the RxThreadTest as an example of how to utilize multiple threads to process a single dependency graph.
