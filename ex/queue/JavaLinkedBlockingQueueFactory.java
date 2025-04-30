package ex.queue;

public class JavaLinkedBlockingQueueFactory<T> implements ex.BlockingQueueFactory<T> {
    @Override
    public String name() {
        return "JavaLinkedBlockingQueue";
    }

    @Override
    public JavaLinkedBlockingQueue<T> create() {
        return new JavaLinkedBlockingQueue<>();
    }
}

class JavaLinkedBlockingQueue<T> extends java.util.concurrent.LinkedBlockingQueue<T> implements ex.BlockingQueue<T> {
    public JavaLinkedBlockingQueue() {
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
