package ex;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        ex.affinity.Affinity.setCores(new int[] {0, 1, 2, 3, 4, 5});

        var queue_factories = new ArrayList<QueueFactory<Integer>>();
        queue_factories.add(new ex.queue.JavaArrayDequeFactory<>(128));
        queue_factories.add(new ex.queue.JavaArrayBlockingQueueFactory<>(128));
        queue_factories.add(new ex.queue.JavaConcurrentLinkedQueueFactory<>());
        queue_factories.add(new ex.queue.JavaLinkedListFactory<>());
        queue_factories.add(new ex.queue.JavaLinkedBlockingQueueFactory<>());
        queue_factories.add(new ex.queue.ConcurrentArrayQueueFactory<>(128));
        queue_factories.add(new ex.queue.LinkedListFactory<>());

        var decorators = new ArrayList<QueueFactoryDecorator>();
        decorators.add(new ex.decorator.SpinLockDecorator());
        decorators.add(new ex.decorator.TicketLockDecorator());
        decorators.add(new ex.decorator.SynchronizedDecorator());
        decorators.add(new ex.decorator.LockDecorator());
        decorators.add(new ex.decorator.Lock2Decorator());

        var benches = new ArrayList<Bench>();
        benches.add(new ex.bench.BaseBench());
        benches.add(new ex.bench.BurstConcurrentReadBench(2));
        benches.add(new ex.bench.BurstConcurrentWriteBench(2));
        benches.add(new ex.bench.BurstBlockingWriteBench(2));
        benches.add(new ex.bench.BurstBlockingWriteBench(2));
        benches.add(new ex.bench.BackloggedConcurrentBench(1, 1));
        benches.add(new ex.bench.BackloggedConcurrentBench(1, 3));
        benches.add(new ex.bench.BackloggedConcurrentBench(3, 1));
        benches.add(new ex.bench.BackloggedConcurrentBench(3, 3));
        benches.add(new ex.bench.BackloggedBlockingBench(1, 1));
        benches.add(new ex.bench.BackloggedBlockingBench(1, 3));
        benches.add(new ex.bench.BackloggedBlockingBench(3, 1));
        benches.add(new ex.bench.BackloggedBlockingBench(3, 3));

        for (int i = 0; i < queue_factories.size(); ++i) {
            for (var decorator : decorators) {
                var factory = queue_factories.get(i);
                var decorated = decorator.decorate(factory);
                if (decorated == null) continue;
                queue_factories.add(decorated);
            }
        }

        long nop = 1000000000;
        for (int i = 0; i < 1_000_000; ++i) {
            var start = System.nanoTime();
            var end = System.nanoTime();
            nop = Math.min(nop, end - start);
        }
        System.err.println("nop: " + nop + "ns");

        for (var queue_factory : queue_factories) {
            for (var bench : benches) {
                var warmup = bench.run(queue_factory, nop);
                if (warmup == null) continue;

                var res = bench.run(queue_factory, nop);
                assert res != null;
                Arrays.sort(res);

                System.out.printf(
                    "%-50s %-50s %10.2f %10.2f %10.2f %10.2f %10.2f\n",
                    bench.name(),
                    queue_factory.name(),
                    res[res.length * 0 / 4],
                    res[res.length * 1 / 4],
                    res[res.length * 2 / 4],
                    res[res.length * 3 / 4],
                    res[res.length - 1]
                );
            }
        }
    }
}
