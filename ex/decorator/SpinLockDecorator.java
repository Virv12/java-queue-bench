package ex.decorator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SpinLockDecorator implements ex.QueueFactoryDecorator {
    @Override
    public <T> ex.QueueFactory<T> decorate(ex.QueueFactory<T> factory) {
        if (factory instanceof ex.ConcurrentQueueFactory) return null;
        return new SpinLockFactory<>(factory);
    }
}

class SpinLockFactory<T> implements ex.ConcurrentQueueFactory<T> {
    private ex.QueueFactory<T> inner;

    public SpinLockFactory(ex.QueueFactory<T> inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return "SpinLock(" + inner.name() + ")";
    }

    @Override
    public SpinLock<T> create() {
        return new SpinLock<>(inner.create());
    }
}

class SpinLock<T> implements ex.ConcurrentQueue<T> {
    private AtomicBoolean lock = new AtomicBoolean(false);
    private ex.Queue<T> inner;

    public SpinLock(ex.Queue<T> inner) {
        this.inner = inner;
    }

    private <U> U with_lock(Supplier<U> supplier) {
        while (lock.getAndSet(true))
            ;
        try {
            return supplier.get();
        } finally {
            lock.set(false);
        }
    }

    @Override
    public boolean try_push(T item) {
        return with_lock(() -> inner.try_push(item));
    }

    @Override
    public T try_pop() {
        return with_lock(() -> inner.try_pop());
    }
}
