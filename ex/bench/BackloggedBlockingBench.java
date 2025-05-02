package ex.bench;

import java.util.ArrayList;

public class BackloggedBlockingBench implements ex.Bench {
    private final int read;
    private final int write;

    public BackloggedBlockingBench(int read, int write) {
        this.read = read;
        this.write = write;
    }

    @Override
    public String name() {
        return "BackloggedBlockingBench(" + read + ", " + write + ")";
    }

    @Override
    public Double run(ex.QueueFactory<Integer> f) throws Exception {
        if (!(f instanceof ex.BlockingQueueFactory)) return null;
        var factory = (ex.BlockingQueueFactory<Integer>)f;
        var queue = factory.create();

        var threads = new ArrayList<Thread>();
        for (int i = 0; i < read; ++i) {
            threads.add(ex.affinity.Affinity.pinned(() -> {
                try {
                    for (int cnt = 0; cnt < 1_000_000 * write; cnt++) {
                        queue.wait_pop();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        for (int i = 0; i < write; ++i) {
            threads.add(ex.affinity.Affinity.pinned(() -> {
                try {
                    for (int cnt = 0; cnt < 1_000_000 * read; cnt++) {
                        queue.wait_push(0);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
