package ex;

public interface QueueFactory<T> extends Named {
    Queue<T> create();
}
