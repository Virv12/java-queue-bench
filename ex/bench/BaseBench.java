package ex.bench;

public class BaseBench implements ex.Bench {
    @Override
    public String name() {
        return "BaseBench";
    }

    @Override
    public Double run(ex.QueueFactory<Integer> factory) {
        var queue = factory.create();
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            queue.try_push(0);
            queue.try_pop();
        }
        long end = System.nanoTime();
        return (end - start) / 1_000_000.0;
    }
}
