package ex.decorator;

public class SynchronizedDecorator implements ex.QueueFactoryDecorator {
    @Override
    public <T> ex.QueueFactory<T> decorate(ex.QueueFactory<T> factory) {
        if (factory instanceof ex.ConcurrentQueueFactory) return null;
        return new SynchronizedFactory<>(factory);
    }
}

class SynchronizedFactory<T> implements ex.BlockingQueueFactory<T> {
    private ex.QueueFactory<T> inner;

    public SynchronizedFactory(ex.QueueFactory<T> inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return "Synchronized(" + inner.name() + ")";
    }

    @Override
    public Synchronized<T> create() {
        return new Synchronized<>(inner.create());
    }
}

class Synchronized<T> implements ex.BlockingQueue<T> {
    private ex.Queue<T> inner;

    public Synchronized(ex.Queue<T> inner) {
        this.inner = inner;
    }

    @Override
    public synchronized boolean try_push(T item) {
        var ret = inner.try_push(item);
        if (ret) notifyAll();
        return ret;
    }

    @Override
    public synchronized T try_pop() {
        var ret = inner.try_pop();
        if (ret != null) notifyAll();
        return ret;
    }

    @Override
    public synchronized void wait_push(T item) throws InterruptedException {
        while (!try_push(item)) {
            wait();
        }
        notifyAll();
    }

    @Override
    public synchronized T wait_pop() throws InterruptedException {
        T item;
        while ((item = try_pop()) == null) {
            wait();
        }
        notifyAll();
        return item;
    }
}
