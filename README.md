# Java Queue Bench

This project is a Java-based benchmarking tool for evaluating the performance of different queue implementations.

## Interfaces

The project includes the following interfaces:

### Queue

The `Queue` interface defines the basic operations for a queue, including:
- `try_push`: Attempts to add an element to the queue, if successful, returns true.
- `try_pop`: Attempts to remove an element from the queue, if successful, returns the element.

### ConcurrentQueue

The `ConcurrentQueue` interface extends the `Queue` interface, it does not provide additional methods but indicates that the queue is designed for concurrent access without blocking.

### BlockingQueue

The `BlockingQueue` interface extends the `ConcurrentQueue` interface and provides additional methods for blocking operations:
- `wait_push`: Blocks until an element can be added to the queue.
- `wait_pop`: Blocks until an element can be removed from the queue.

### QueueFactory

The `QueueFactory` interface provides a method for creating instances of queues:
- `create`: Creates a new instance of a queue.

### ConcurrentQueueFactory

The `ConcurrentQueueFactory` interface extends the `QueueFactory` interface and specializes the `create` method for creating concurrent queues.

### BlockingQueueFactory

The `BlockingQueueFactory` interface extends the `ConcurrentQueueFactory` interface and specializes the `create` method for creating blocking queues.

### QueueFactoryDecorator

The `QueueFactoryDecorator` interface provides a method for creating decorated queue factories with additional functionality:
- `decorate`: Decorates a queue factory with additional functionality and returns a new instance of the decorated factory or null if the factory cannot be decorated.

### Bench

The `Bench` interface provides methods for running benchmarks on queue implementations:
- `run`: Runs the benchmark on the specified queue implementation and returns an array of results or null if the benchmark does not support the specified queue.

## Queues

### JavaArrayBlockingQueue
A wrapper around `java.util.concurrent.ArrayBlockingQueue` that implements the `BlockingQueue` interface.

### JavaArrayDeque
A wrapper around `java.util.ArrayDeque` that implements the `Queue` interface.

### JavaConcurrentLinkedQueue
A wrapper around `java.util.concurrent.ConcurrentLinkedQueue` that implements the `ConcurrentQueue` interface.

### JavaLinkedBlockingQueue
A wrapper around `java.util.concurrent.LinkedBlockingQueue` that implements the `BlockingQueue` interface.

### JavaLinkedList
A wrapper around `java.util.LinkedList` that implements the `Queue` interface.

### ConcurrentArrayQueue
A concurrent lock-free array-based queue implementation that implements the `ConcurrentQueue` interface.

### LinkedList
A linked list-based queue implementation that implements the `Queue` interface.

## Decorators

### SpinLockDecorator
A spinlock implementation that can be used to enhance queue operations with lock-free behavior.

### TicketLockDecorator
A ticket lock implementation that can be used to enhance queue operations with lock-free behavior.

### SynchronizedDecorator
A synchronized decorator that provides thread-safe access to queue operations using Java's built-in synchronization mechanisms.

### LockDecorator
A lock decorator that provides thread-safe access to queue operations using Java's `ReentrantLock`.

### Lock2Decorator
A lock decorator that provides thread-safe access to queue operations using Java's `ReentrantLock` with two Condition objects.

## Benchmarks

### BaseBench
A simple benchmark measuring the performance of queue operations in a single-threaded environment.

### BackloggedConcurrentBench(r,w)
A benchmark with `r+w` threads, where `r` are readers and `w` are writers.
It measures the performance of concurrent queue operations with backlogged readers and writers.

Analogous benchmarks is available for `BackloggedBlockingBench`.

### BurstBlockingReadBench(w)
A benchmark with `w+1` threads.
`w` threads are writing to a blocking queue in a loop, while one thread is reading one element every 10us.
It measures the performance of each read operation.

Analogous benchmarks are available for `BurstBlockingWriteBench`, `BurstConcurrentReadBench` and `BurstConcurrentWriteBench`.
