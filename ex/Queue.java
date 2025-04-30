package ex;

public interface Queue<T> {
    boolean try_push(T item);
    T try_pop();
}
