package ex;

public interface Bench extends Named {
    Double run(QueueFactory<Integer> queue, long nop) throws Exception;
}
