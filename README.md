# Java Queue Bench

This project is a Java-based benchmarking tool for evaluating the performance of different queue implementations.

## Interfaces

The project includes the following interfaces:
- `Queue`: A generic interface for queue operations.
- `QueueFactory`: A factory interface for creating queue instances.
- `ConcurrentQueue`: A concurrent queue interface for thread-safe operations without blocking.
- `ConcurrentQueueFactory`: A factory interface for creating concurrent queue instances.
- `BlockingQueue`: A blocking queue interface for thread-safe operations with blocking behavior.
- `BlockingQueueFactory`: A factory interface for creating blocking queue instances.
- `QueueFactoryDecorator`: A decorator interface for enhancing queue factories with additional functionality.
- `Bench`: A benchmarking interface for measuring the performance of queue operations.

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

### SynchronizedDecorator
A synchronized decorator that provides thread-safe access to queue operations using Java's built-in synchronization mechanisms.

### LockDecorator
A lock decorator that provides thread-safe access to queue operations using Java's `ReentrantLock`.

### Lock2Decorator
A lock decorator that provides thread-safe access to queue operations using Java's `ReentrantLock` with two Condition objects.

## Benchmarks

### BaseBench
A simple benchmark measuring the performance of queue operations in a single-threaded environment.

### BackloggedConcurrentBench
A benchmark measuring the performance of concurrent queue operations with backlogged tasks.

### BackloggedBlockingBench
A benchmark measuring the performance of blocking queue operations with backlogged tasks.

### BurstBlockingReadBench
A benchmark measuring the performance of periodic reads from a blocking queue with backlogged writers.

### BurstBlockingWriteBench
A benchmark measuring the performance of periodic writes to a blocking queue with backlogged readers.

### BurstConcurrentReadBench
A benchmark measuring the performance of periodic reads from a concurrent queue with backlogged writers.

### BurstConcurrentWriteBench
A benchmark measuring the performance of periodic writes to a concurrent queue with backlogged readers.
