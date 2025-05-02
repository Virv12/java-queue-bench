package ex.affinity;

import java.util.ArrayList;
import java.util.Arrays;

public class Affinity {
    private static ArrayList<Integer> cores;

    static {
        System.loadLibrary("Affinity");
    }

    private static native int setAffinity_impl(int affinity);

    private static synchronized int allocCore() {
        if (cores.isEmpty()) {
            throw new RuntimeException("No cores available");
        }
        return cores.remove(cores.size() - 1);
    }

    private static synchronized void freeCore(int core) {
        cores.add(core);
    }

    private static void setAffinity(int affinity) {
        int ret = setAffinity_impl(affinity);
        if (ret != 0) {
            throw new RuntimeException("Failed to set CPU affinity");
        }
    }

    public static synchronized void setCores(int[] coreArray) {
        cores = new ArrayList<>();
        for (int core : coreArray) {
            cores.add(core);
        }
    }

    public static Thread pinned(Runnable runnable) {
        Thread thread = new Thread(() -> {
            int core = allocCore();
            try {
                setAffinity(core);
                runnable.run();
            } finally {
                freeCore(core);
            }
        });
        return thread;
    }
}
