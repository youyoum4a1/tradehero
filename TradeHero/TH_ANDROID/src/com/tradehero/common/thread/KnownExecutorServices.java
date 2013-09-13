package com.tradehero.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 3:56 PM
 */
public class KnownExecutorServices
{
    public static final int DEFAULT_THREAD_COUNT = 1;

    private static int cpuThreadCount = DEFAULT_THREAD_COUNT;
    private static ExecutorService mCPUExecutor;
    private static int cacheThreadCount = DEFAULT_THREAD_COUNT;
    private static ExecutorService mCacheExecutor;

    public static int getCpuThreadCount()
    {
        return cpuThreadCount;
    }

    public static void setCpuThreadCount(int cpuThreadCount)
    {
        KnownExecutorServices.cpuThreadCount = cpuThreadCount;
    }

    public static int getCacheThreadCount()
    {
        return cacheThreadCount;
    }

    public static void setCacheThreadCount(int cacheThreadCount)
    {
        KnownExecutorServices.cacheThreadCount = cacheThreadCount;
    }

    /**
     * Reserve this executor to CPU intensive runnables.
     * @return
     */
    public static ExecutorService getCPUExecutor()
    {
        if (mCPUExecutor == null)
        {
            mCPUExecutor = Executors.newFixedThreadPool(cpuThreadCount);
        }
        return mCPUExecutor;
    }

    /**
     * Reserve this executor to Cache intensive runnables.
     * @return
     */
    public static ExecutorService getCacheExecutor()
    {
        if (mCacheExecutor == null)
        {
            mCacheExecutor = Executors.newFixedThreadPool(cacheThreadCount);
        }
        return mCacheExecutor;
    }

}
