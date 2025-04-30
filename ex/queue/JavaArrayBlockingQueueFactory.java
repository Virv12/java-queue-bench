package ex.queue;

public class JavaArrayBlockingQueueFactory<T> implements ex.BlockingQueueFactory<T> {
    private final int capacity;

    public JavaArrayBlockingQueueFactory(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String name() {
        return "JavaArrayBlockingQueue(" + capacity + ")";
    }

    @Override
    public JavaArrayBlockingQueue<T> create() {
        return new JavaArrayBlockingQueue<>(capacity);
    }
}

class JavaArrayBlockingQueue<T> extends java.util.concurrent.ArrayBlockingQueue<T> implements ex.BlockingQueue<T> {
    public JavaArrayBlockingQueue(int capacity) {
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

    @Override
    public void wait_push(T item) throws InterruptedException {
        put(item);
    }

    @Override
    public T wait_pop() throws InterruptedException {
        return take();
    }
}
