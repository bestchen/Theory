package cn.it.com.theroy.mamager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPool {

    private static ThreadPool sPool;

    public static ThreadPool getThreadPool() {
        if (sPool == null) {
            synchronized (ThreadPool.class) {
                if (sPool == null) {
                    sPool = new ThreadPool();
                }
            }
        }
        return sPool;
    }

    public ThreadPool() {
    }

    private ThreadPoolExecutor threadPoolExecutor;

    private final static int SIZE_POOL_CORE = 2;
    private final static int SIZE_POLL_MAX = 10;
    private final static int TIME_KEEP_ALIVE = 2;
    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public void execute(Runnable task) {
        if (threadPoolExecutor == null) {
            initThreadPoolExecutor();
        }
        threadPoolExecutor.execute(task);
    }

    public void shutdown() {
        if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown()) {
            threadPoolExecutor.shutdownNow();
            threadPoolExecutor = null;
        }
    }

    private void initThreadPoolExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(SIZE_POOL_CORE, SIZE_POLL_MAX, TIME_KEEP_ALIVE, TIME_UNIT, new LinkedBlockingQueue<Runnable>(), new RejectedHandler());
    }

    private static class RejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                r.run();
            }
        }
    }
}
