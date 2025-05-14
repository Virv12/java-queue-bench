package ex.bench;

import java.util.ArrayList;

public class BurstBlockingReadBench implements ex.Bench {
    private final int write;

    public BurstBlockingReadBench(int write) {
        this.write = write;
    }

    @Override
    public String name() {
        return "BurstBlockingReadBench(" + write + ")";
    }

    @Override
    public double[] run(ex.QueueFactory<Integer> f, long nop) throws Exception {
        if (!(f instanceof ex.BlockingQueueFactory)) return null;
        var factory = (ex.BlockingQueueFactory<Integer>)f;
        var queue = factory.create();

        var writers = new ArrayList<Thread>();
        for (int i = 0; i < write; ++i) {
            writers.add(ex.affinity.Affinity.pinned(() -> {
                try {
                    var th = Thread.currentThread();
                    while (!th.isInterrupted()) {
                        queue.wait_push(0);
                    }
                } catch (InterruptedException e) {
                }
            }));
        }

        var reader_runnable = new Runnable() {
            double[] data = null;

            @Override
            public void run() {
                try {
                    var d = new double[100_000];
                    for (int i = 0; i < 100_000; ++i) {
                        for (var x = System.nanoTime(); System.nanoTime() < x + 10_000; )
                            ;
                        long start = System.nanoTime();
                        var popped = queue.wait_pop();
                        assert popped != null;
                        long end = System.nanoTime();
                        d[i] = end - start - nop;
                    }
                    data = d;
                } catch (InterruptedException e) {
                }
            }
        };
        var reader = ex.affinity.Affinity.pinned(reader_runnable);

        for (var thread : writers) {
            thread.start();
        }
        Thread.sleep(1);
        reader.start();
        reader.join();
        for (var thread : writers) {
            thread.interrupt();
        }
        for (var thread : writers) {
            thread.join();
        }
        return reader_runnable.data;
    }
}
