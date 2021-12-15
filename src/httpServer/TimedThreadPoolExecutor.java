package httpServer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimedThreadPoolExecutor extends ThreadPoolExecutor {
    public TimedThreadPoolExecutor(int poolSize, int timeoutMs) {
        super(poolSize,
                poolSize,
                timeoutMs,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    public boolean shutdown(long awaitMs) {
        long start = System.currentTimeMillis();
        while (getActiveCount() > 0) {
            if (System.currentTimeMillis() - start >= awaitMs) {
                shutdown();
                return false;
            }
        }
        return true;
    }
}
