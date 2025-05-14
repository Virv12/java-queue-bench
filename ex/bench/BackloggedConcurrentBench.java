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
        return "BackloggedConcurrentBench(" + read + "," + write + ")";
    }

    @Override
    public double[] run(ex.QueueFactory<Integer> f, long nop) throws Exception {
        if (!(f instanceof ex.ConcurrentQueueFactory)) return null;
        var factory = (ex.ConcurrentQueueFactory<Integer>)f;

        var data = new double[50];
        for (int idx = 0; idx < data.length; ++idx) {
            var queue = factory.create();

            var threads = new ArrayList<Thread>();
            for (int i = 0; i < read; ++i) {
                threads.add(ex.affinity.Affinity.pinned(() -> {
                    int cnt = 0;
                    while (cnt < 30_000 * write) {
                        cnt += queue.try_pop() != null ? 1 : 0;
                    }
                }));
            }
            for (int i = 0; i < write; ++i) {
                threads.add(ex.affinity.Affinity.pinned(() -> {
                    int cnt = 0;
                    while (cnt < 30_000 * read) {
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
            data[idx] = (end - start - nop) / (30_000.0 * read * write);
        }
        return data;
    }
}
