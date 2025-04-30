package ex;

public interface ConcurrentQueueFactory<T> extends QueueFactory<T> {
    @Override
    ConcurrentQueue<T> create();
}
