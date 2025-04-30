package ex.queue;

public class JavaArrayDequeFactory<T> implements ex.QueueFactory<T> {
    private final int capacity;

    public JavaArrayDequeFactory(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String name() {
        return "JavaArrayDeque(" + capacity + ")";
    }

    @Override
    public JavaArrayDeque<T> create() {
        return new JavaArrayDeque<>(capacity);
    }
}

class JavaArrayDeque<T> extends java.util.ArrayDeque<T> implements ex.Queue<T> {
    public JavaArrayDeque(int capacity) {
        super(capacity);
    }

    @Override
    public boolean try_push(T item) {
        return offer(item);
    }

    @Override
    public T try_pop() {
        return poll();
    }
}
