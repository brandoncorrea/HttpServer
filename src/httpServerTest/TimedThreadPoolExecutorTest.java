package httpServerTest;

import httpServer.TimedThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimedThreadPoolExecutorTest {
    @Test
    public void newExecutor() {
        for (int[] options : new int[][] {{100, 30000}, {3, 10}}) {
            int size = options[0];
            int timeout = options[1];
            TimedThreadPoolExecutor executor = new TimedThreadPoolExecutor(size, timeout);
            Assert.assertEquals(size, executor.getCorePoolSize());
            Assert.assertEquals(size, executor.getMaximumPoolSize());
            Assert.assertEquals(timeout, executor.getKeepAliveTime(TimeUnit.MILLISECONDS));
            Assert.assertEquals(0, executor.getQueue().size());
        }
    }

    @Test
    public void shutdownAwaitsTimespan() throws InterruptedException {
        TimedThreadPoolExecutor executor = new TimedThreadPoolExecutor(100, 30000);
        long start = System.currentTimeMillis();
        boolean result = executor.shutdown(3000);
        long end = System.currentTimeMillis();
        Assert.assertTrue(end - start <= 10);
        Assert.assertTrue(result);

        executor = new TimedThreadPoolExecutor(100, 30000);
        executor.submit(() -> {
            try {
                Thread.sleep(30000);
            } catch (Exception ignored) { }
        });

        // Wait 10ms for executor to start task
        Thread.sleep(10);

        start = System.currentTimeMillis();
        result = executor.shutdown(1000);
        end = System.currentTimeMillis();
        long diff = end - start;
        Assert.assertFalse(result);
        Assert.assertTrue(1000 <= diff && diff <= 1050);
    }
}
