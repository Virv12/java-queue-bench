package ex.bench;

import java.util.ArrayList;

public class BurstConcurrentReadBench implements ex.Bench {
    private final int write;

    public BurstConcurrentReadBench(int write) {
        this.write = write;
    }

    @Override
    public String name() {
        return "BurstConcurrentReadBench(" + write + ")";
    }

    @Override
    public Double run(ex.QueueFactory<Integer> f, long nop) throws Exception {
        if (!(f instanceof ex.ConcurrentQueueFactory)) return null;
        var factory = (ex.ConcurrentQueueFactory<Integer>)f;
        var queue = factory.create();

        var writers = new ArrayList<Thread>();
        for (int i = 0; i < write; ++i) {
            writers.add(ex.affinity.Affinity.pinned(() -> {
                var th = Thread.currentThread();
                while (!th.isInterrupted()) {
                    queue.try_push(0);
                }
            }));
        }

        var reader_runnable = new Runnable() {
            long time = 0;

            @Override
            public void run() {
                try {
                    for (int cnt = 0; cnt < 1_000_000; ++cnt) {
                        Thread.sleep(0, 20_000);
                        long start = System.nanoTime();
                        var popped = queue.try_pop();
                        assert popped != null;
                        long end = System.nanoTime();
                        time += end - start - nop;
                    }
                } catch (InterruptedException e) {
                    time = -1;
                }
            }
        };
        var reader = ex.affinity.Affinity.pinned(reader_runnable);

        for (var thread : writers) {
            thread.start();
        }
        reader.start();
        reader.join();
        for (var thread : writers) {
            thread.interrupt();
        }
        for (var thread : writers) {
            thread.join();
        }
        return reader_runnable.time / 1_000_000.0;
    }
}
