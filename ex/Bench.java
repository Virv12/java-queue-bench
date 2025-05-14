package ex;

public interface Bench extends Named {
    double[] run(QueueFactory<Integer> queue, long nop) throws Exception;
}
