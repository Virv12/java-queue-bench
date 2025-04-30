package ex.queue;

public class LinkedListFactory<T> implements ex.QueueFactory<T> {
    @Override
    public String name() {
        return "LinkedList";
    }

    @Override
    public LinkedList<T> create() {
        return new LinkedList<>();
    }
}

class LinkedList<T> implements ex.Queue<T> {
    private Node<T> head;

    public LinkedList() {
        head = new Node<>(null);
    }

    @Override
    public boolean try_push(T v) {
        var n = new Node<>(v);
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
        return true;
    }

    @Override
    public T try_pop() {
        var back = head.prev;
        if (back == head) return null;
        T v = back.val;
        back.prev.next = head;
        head.prev = back.prev;
        return v;
    }
}

class Node<T> {
    Node<T> next;
    Node<T> prev;
    T val;

    Node(T v) {
        next = this;
        prev = this;
        val = v;
    }
}
