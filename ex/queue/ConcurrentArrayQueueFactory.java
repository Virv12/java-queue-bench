package ex.queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ConcurrentArrayQueueFactory<T> implements ex.ConcurrentQueueFactory<T> {
    private final int capacity;

    public ConcurrentArrayQueueFactory(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String name() {
        return "ConcurrentArrayQueue(" + capacity + ")";
    }

    @Override
    public ConcurrentArrayQueue<T> create() {
        return new ConcurrentArrayQueue<>(capacity);
    }
}

class ConcurrentArrayQueue<T> implements ex.ConcurrentQueue<T> {
    private List<T> buffer;
    private AtomicInteger push_done, push_op, push_avail;
    private AtomicInteger pop_done, pop_op, pop_avail;

    public ConcurrentArrayQueue(int capacity) {
        buffer = new ArrayList<>(Collections.nCopies(capacity, null));
        push_done = new AtomicInteger(0);
        push_op = new AtomicInteger(0);
        push_avail = new AtomicInteger(capacity);
        pop_done = new AtomicInteger(0);
        pop_op = new AtomicInteger(0);
        pop_avail = new AtomicInteger(0);
    }

    @Override
    public boolean try_push(T item) {
        int pa = push_avail.getAndDecrement();
        if (pa <= 0) {
            push_avail.getAndIncrement();
            return false;
        }

        int po = push_op.getAndIncrement();
        buffer.set(po % buffer.size(), item);

        while (!push_done.compareAndSet(po, po + 1))
            ;
        pop_avail.getAndIncrement();

        return true;
    }

    @Override
    public T try_pop() {
        int pa = pop_avail.getAndDecrement();
        if (pa <= 0) {
            pop_avail.getAndIncrement();
            return null;
        }

        int po = pop_op.getAndIncrement();
        var item = buffer.get(po % buffer.size());
        buffer.set(po % buffer.size(), null);

        while (!pop_done.compareAndSet(po, po + 1))
            ;
        push_avail.getAndIncrement();

        return item;
    }
}
