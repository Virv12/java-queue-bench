package ex.queue;

public class JavaLinkedListFactory<T> implements ex.QueueFactory<T> {
    @Override
    public String name() {
        return "JavaLinkedList";
    }

    @Override
    public JavaLinkedList<T> create() {
        return new JavaLinkedList<>();
    }
}

class JavaLinkedList<T> extends java.util.LinkedList<T> implements ex.Queue<T> {
    public JavaLinkedList() {
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
