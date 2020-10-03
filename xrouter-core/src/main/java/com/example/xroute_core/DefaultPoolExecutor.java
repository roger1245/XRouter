package com.example.xroute_core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultPoolExecutor {
    public static ThreadPoolExecutor executor;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "XRouter #" + mCount.getAndIncrement());
        }
    };

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAX_CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final long SURPLUS_THREAD_LIFE = 30L;

    public static ThreadPoolExecutor newDefaultPoolExecutor(int corePoolSize) {
        if (corePoolSize == 0) {
            return null;
        }
        corePoolSize = Math.min(corePoolSize, MAX_CORE_POOL_SIZE);
        executor = new ThreadPoolExecutor(corePoolSize,
                corePoolSize, SURPLUS_THREAD_LIFE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64), sThreadFactory);
        executor.allowCoreThreadTimeOut(true);
        return executor;


    }
}
