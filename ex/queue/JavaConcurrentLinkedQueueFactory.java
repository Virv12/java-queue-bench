package ex.queue;

public class JavaConcurrentLinkedQueueFactory<T> implements ex.ConcurrentQueueFactory<T> {
    @Override
    public String name() {
        return "JavaConcurrentLinkedQueue";
    }

    @Override
    public JavaConcurrentLinkedQueue<T> create() {
        return new JavaConcurrentLinkedQueue<>();
    }
}

class JavaConcurrentLinkedQueue<T> extends java.util.concurrent.ConcurrentLinkedQueue<T> implements ex.ConcurrentQueue<T> {
    @Override
    public boolean try_push(T item) {
        return offer(item);
    }

    @Override
    public T try_pop() {
        return poll();
    }
}
