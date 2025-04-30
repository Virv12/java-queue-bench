package ex;

public interface BlockingQueue<T> extends ConcurrentQueue<T> {
    void wait_push(T item) throws InterruptedException;
    T wait_pop() throws InterruptedException;
}
