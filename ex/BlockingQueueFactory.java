package ex;

public interface BlockingQueueFactory<T> extends ConcurrentQueueFactory<T> {
    @Override
    BlockingQueue<T> create();
}
