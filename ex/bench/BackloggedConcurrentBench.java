package ex.bench;

import java.util.ArrayList;

public class BackloggedConcurrentBench implements ex.Bench {
    private final int read;
    private final int write;

    public BackloggedConcurrentBench(int read, int write) {
        this.read = read;
        this.write = write;
    }

    @Override
    public String name() {
        return "BackloggedConcurrentBench(" + read + ", " + write + ")";
    }

    @Override
    public Double run(ex.QueueFactory<Integer> f) throws Exception {
        if (!(f instanceof ex.ConcurrentQueueFactory)) return null;
        var factory = (ex.ConcurrentQueueFactory<Integer>)f;
        var queue = factory.create();

        var threads = new ArrayList<Thread>();
        for (int i = 0; i < read; ++i) {
            threads.add(ex.affinity.Affinity.pinned(() -> {
                int cnt = 0;
                while (cnt < 1_000_000 * write) {
                    cnt += queue.try_pop() != null ? 1 : 0;
                }
            }));
        }
        for (int i = 0; i < write; ++i) {
            threads.add(ex.affinity.Affinity.pinned(() -> {
                int cnt = 0;
                while (cnt < 1_000_000 * read) {
                    cnt += queue.try_push(0) ? 1 : 0;
                }
            }));
        }

        long start = System.nanoTime();
        for (var thread : threads) {
            thread.start();
        }
        for (var thread : threads) {
            thread.join();
        }
        long end = System.nanoTime();
        return (end - start) / (1_000_000.0 * read * write);
    }
}
