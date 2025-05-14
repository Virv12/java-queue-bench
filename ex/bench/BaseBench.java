package ex.bench;

public class BaseBench implements ex.Bench {
    @Override
    public String name() {
        return "BaseBench";
    }

    @Override
    public double[] run(ex.QueueFactory<Integer> factory, long nop) {
        var queue = factory.create();
        var data = new double[100_000];
        for (int i = 0; i < 100_000; i++) {
            long start = System.nanoTime();
            queue.try_push(0);
            queue.try_pop();
            long end = System.nanoTime();
            data[i] = end - start - nop;
        }
        return data;
    }
}
