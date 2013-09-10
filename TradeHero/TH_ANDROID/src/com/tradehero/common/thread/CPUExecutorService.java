package com.tradehero.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 3:56 PM
 * Reserve this executor to CPU intensive runnables.
 */
public class CPUExecutorService
{
    public static final int DEFAULT_THREAD_COUNT = 1;

    private static int threadCount = DEFAULT_THREAD_COUNT;
    private static ExecutorService mExecutor;

    public static int getThreadCount()
    {
        return threadCount;
    }

    public static void setThreadCount(int threadCount)
    {
        CPUExecutorService.threadCount = threadCount;
    }

    public static ExecutorService getExecutor()
    {
        if (mExecutor == null)
        {
            mExecutor = Executors.newFixedThreadPool(threadCount);
        }
        return mExecutor;
    }
}
