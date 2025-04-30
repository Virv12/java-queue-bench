package ex.decorator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.function.Supplier;

public class Lock2Decorator implements ex.QueueFactoryDecorator {
    @Override
    public <T> ex.QueueFactory<T> decorate(ex.QueueFactory<T> factory) {
        if (factory instanceof ex.ConcurrentQueueFactory) return null;
        return new Lock2Factory<>(factory);
    }
}

class Lock2Factory<T> implements ex.BlockingQueueFactory<T> {
    private ex.QueueFactory<T> inner;

    public Lock2Factory(ex.QueueFactory<T> inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return "Lock2(" + inner.name() + ")";
    }

    @Override
    public Lock2<T> create() {
        return new Lock2<>(inner.create());
    }
}

class Lock2<T> implements ex.BlockingQueue<T> {
    private ReentrantLock lock;
    private Condition full, empty;
    private ex.Queue<T> inner;

    public Lock2(ex.Queue<T> inner) {
        this.lock = new ReentrantLock();
        this.full = lock.newCondition();
        this.empty = lock.newCondition();
        this.inner = inner;
    }

    @Override
    public boolean try_push(T item) {
        lock.lock();
        try {
            if (inner.try_push(item)) {
                empty.signal();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T try_pop() {
        lock.lock();
        try {
            T item = inner.try_pop();
            if (item != null) {
                full.signal();
            }
            return item;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void wait_push(T item) throws InterruptedException {
        lock.lock();
        try {
            while (!inner.try_push(item)) {
                full.await();
            }
            empty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T wait_pop() throws InterruptedException {
        lock.lock();
        try {
            T item;
            while ((item = inner.try_pop()) == null) {
                empty.await();
            }
            full.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
