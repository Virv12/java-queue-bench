package ex.decorator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TicketLockDecorator implements ex.QueueFactoryDecorator {
    @Override
    public <T> ex.QueueFactory<T> decorate(ex.QueueFactory<T> factory) {
        if (factory instanceof ex.ConcurrentQueueFactory) return null;
        return new TicketLockFactory<>(factory);
    }
}

class TicketLockFactory<T> implements ex.ConcurrentQueueFactory<T> {
    private ex.QueueFactory<T> inner;

    public TicketLockFactory(ex.QueueFactory<T> inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return "TicketLock(" + inner.name() + ")";
    }

    @Override
    public TicketLock<T> create() {
        return new TicketLock<>(inner.create());
    }
}

class TicketLock<T> implements ex.ConcurrentQueue<T> {
    private AtomicInteger enter = new AtomicInteger();
    private AtomicInteger exit = new AtomicInteger();
    private ex.Queue<T> inner;

    public TicketLock(ex.Queue<T> inner) {
        this.inner = inner;
    }

    private <U> U with_lock(Supplier<U> supplier) {
        var ticket = enter.getAndIncrement();
        while (exit.get() != ticket)
            ;
        try {
            return supplier.get();
        } finally {
            exit.getAndIncrement();
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
