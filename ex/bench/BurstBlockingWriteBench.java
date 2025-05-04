package ex.bench;

import java.util.ArrayList;

public class BurstBlockingWriteBench implements ex.Bench {
    private final int read;

    public BurstBlockingWriteBench(int read) {
        this.read = read;
    }

    @Override
    public String name() {
        return "BurstBlockingWriteBench(" + read + ")";
    }

    @Override
    public Double run(ex.QueueFactory<Integer> f, long nop) throws Exception {
        if (!(f instanceof ex.BlockingQueueFactory)) return null;
        var factory = (ex.BlockingQueueFactory<Integer>)f;
        var queue = factory.create();

        var readers = new ArrayList<Thread>();
        for (int i = 0; i < read; ++i) {
            readers.add(ex.affinity.Affinity.pinned(() -> {
                try {
                    var th = Thread.currentThread();
                    while (!th.isInterrupted()) {
                        queue.wait_pop();
                    }
                } catch (InterruptedException e) {
                }
            }));
        }

        var writer_runnable = new Runnable() {
            long time = 0;

            @Override
            public void run() {
                try {
                    for (int cnt = 0; cnt < 1_000_000; ++cnt) {
                        for (var x = System.nanoTime(); System.nanoTime() < x + 10_000; )
                            ;
                        long start = System.nanoTime();
                        queue.wait_push(0);
                        long end = System.nanoTime();
                        time += end - start - nop;
                    }
                } catch (InterruptedException e) {
                    time = -1;
                }
            }
        };
        var writer = ex.affinity.Affinity.pinned(writer_runnable);

        for (var thread : readers) {
            thread.start();
        }
        writer.start();
        writer.join();
        for (var thread : readers) {
            thread.interrupt();
        }
        for (var thread : readers) {
            thread.join();
        }
        return writer_runnable.time / 1_000_000.0;
    }
}
