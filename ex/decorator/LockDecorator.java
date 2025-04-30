package ex.decorator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.function.Supplier;

public class LockDecorator implements ex.QueueFactoryDecorator {
    @Override
    public <T> ex.QueueFactory<T> decorate(ex.QueueFactory<T> factory) {
        if (factory instanceof ex.ConcurrentQueueFactory) return null;
        return new LockFactory<>(factory);
    }
}

class LockFactory<T> implements ex.BlockingQueueFactory<T> {
    private ex.QueueFactory<T> inner;

    public LockFactory(ex.QueueFactory<T> inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return "Lock(" + inner.name() + ")";
    }

    @Override
    public Lock<T> create() {
        return new Lock<>(inner.create());
    }
}

class Lock<T> implements ex.BlockingQueue<T> {
    private ReentrantLock lock;
    private Condition cond;
    private ex.Queue<T> inner;

    public Lock(ex.Queue<T> inner) {
        this.lock = new ReentrantLock();
        this.cond = lock.newCondition();
        this.inner = inner;
    }

    @Override
    public boolean try_push(T item) {
        lock.lock();
        try {
            if (inner.try_push(item)) {
                cond.signalAll();
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
                cond.signalAll();
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
                cond.await();
            }
            cond.signalAll();
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
                cond.await();
            }
            cond.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
