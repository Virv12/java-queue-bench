# Java Queue Bench

This project is a Java-based benchmarking tool for evaluating the performance
of different queue implementations.

## Interfaces

### Queue

The `Queue` interface defines the basic operations for a queue, including:
- `boolean try_push(T item)`: Attempts to add an element to the queue, if
  successful, returns true.
- `T try_pop()`: Attempts to remove an element from the queue, if successful,
  returns the element.

### ConcurrentQueue

The `ConcurrentQueue` interface extends the `Queue` interface, it does not
provide additional methods but indicates that the queue is designed for
concurrent access without blocking.

### BlockingQueue

The `BlockingQueue` interface extends the `ConcurrentQueue` interface and
provides additional methods for blocking operations:
- `void wait_push(T item) throws InterruptedException`: Blocks until an element
  can be added to the queue.
- `T wait_pop() throws InterruptedException`: Blocks until an element can be
  removed from the queue.

### QueueFactory

The `QueueFactory` interface provides a method for creating instances of
queues:
- `Queue<T> create()`: Creates a new instance of a queue.

### ConcurrentQueueFactory

Specialized factory for `ConcurrentQueue` implementations.

- `@Override ConcurrentQueue<T> create()`

### BlockingQueueFactory

Specialized factory for `BlockingQueue` implementations.

- `@Override BlockingQueue<T> create()`

### QueueFactoryDecorator

The `QueueFactoryDecorator` interface provides a method for creating decorated
queue factories with additional functionality:
- `<T> QueueFactory<T> decorate(QueueFactory<T> factory)`: Decorates a queue
  factory with additional functionality and returns a new instance of the
  decorated factory or null if the factory cannot be decorated.

### Bench

The `Bench` interface provides methods for running benchmarks on queue
implementations:
- `double[] run(QueueFactory<Integer> queue, long nop)`: Runs the benchmark on
  the specified queue implementation and returns an array of results or null if
  the benchmark does not support the specified queue.
  `nop` is the time between two consecutive calls to `System.nanoTime()` in
  nanoseconds.

## Queues

### JavaArrayBlockingQueue
A wrapper around `java.util.concurrent.ArrayBlockingQueue` that implements the
`BlockingQueue` interface.

### JavaArrayDeque
A wrapper around `java.util.ArrayDeque` that implements the `Queue` interface.

### JavaConcurrentLinkedQueue
A wrapper around `java.util.concurrent.ConcurrentLinkedQueue` that implements
the `ConcurrentQueue` interface.

### JavaLinkedBlockingQueue
A wrapper around `java.util.concurrent.LinkedBlockingQueue` that implements the
`BlockingQueue` interface.

### JavaLinkedList
A wrapper around `java.util.LinkedList` that implements the `Queue` interface.

### ConcurrentArrayQueue
A custom, concurrent lock-free array-based queue implementation that implements
the `ConcurrentQueue` interface.

### LinkedList
A custom linked list-based queue implementation that implements the `Queue`
interface.

## Decorators

### SpinLockDecorator
A spinlock implementation, using a single `AtomicBoolean`, that can be used to
enhance `Queue`s into `ConcurrentQueue`s.

### TicketLockDecorator
A ticket lock implementation that can be used to enhance `Queue`s into
`ConcurrentQueue`s.

### SynchronizedDecorator
A synchronized decorator that provides thread-safe and blocking behaviour to
queue operations using Java's built-in synchronization mechanisms.

### LockDecorator
A lock decorator that provides thread-safe and blocking behaviour to queue
operations using Java's `ReentrantLock`.

### Lock2Decorator
A lock decorator that provides thread-safe and blocking behaviour to queue
operations using Java's `ReentrantLock` with two `Condition` objects, one to
wait when the queue is full and the other to wait when the queue is empty.

## Benchmarks

### BaseBench
A simple benchmark measuring the performance of queue operations in a
single-threaded environment.

### BackloggedConcurrentBench(r,w)
A benchmark with `r+w` threads, where `r` are readers and `w` are writers. It
measures the performance of concurrent queue operations with backlogged readers
and writers.

Analogous benchmarks is available for `BackloggedBlockingBench(r,w)`.

### BurstBlockingReadBench(w)
A benchmark with `w+1` threads.
`w` threads are repeatedly writing to a blocking queue, while one thread is
reading one element every 10us.
It measures the performance of each read operation.

Analogous benchmarks are available for `BurstBlockingWriteBench(w)`,
`BurstConcurrentReadBench(r)` and `BurstConcurrentWriteBench(w)`.

## Conclusions

After looking at the benchmarks the following implementations have been
observed delivering consistent good performance.
For basic single-threaded operations: `java.util.ArrayDeque.`.
For concurrent non-blocking operations: `java.util.concu.ConcurrentLinkedQueue`.
For blocking operations: `java.util.concurrent.LinkedBlockingQueue`.
