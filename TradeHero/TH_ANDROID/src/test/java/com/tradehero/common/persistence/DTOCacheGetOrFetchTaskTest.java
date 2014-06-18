package com.tradehero.common.persistence;

import android.os.AsyncTask;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.Transcript;
import timber.log.Timber;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class DTOCacheGetOrFetchTaskTest
{
    private Transcript transcript;
    @Inject AlertCompactListCache alertCompactListCache;

    @Before public void setUp() throws Exception
    {
        transcript = new Transcript();
        Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();
    }

    @After public void tearDown()
    {
        alertCompactListCache.invalidateAll();
        Robolectric.getBackgroundScheduler().unPause();
        Robolectric.getBackgroundScheduler().advanceToLastPostedRunnable();
        Robolectric.getUiThreadScheduler().unPause();
        Robolectric.getUiThreadScheduler().advanceToLastPostedRunnable();
        ((ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR).getQueue().clear();
    }

    @Test
    public void checkNotCrashWhen138TasksQueuedPool()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> task = alertCompactListCache.getOrFetch(new UserBaseKey(userId), null);
            task.executePool();
        }
    }

    @Test(expected = RejectedExecutionException.class)
    public void checkCrashWhen139TasksQueuedPool()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> task = alertCompactListCache.getOrFetch(new UserBaseKey(userId), null);
            task.executePool();
        }
        assertTrue(false);
    }

    @Test
    public void checkNotCrashWhen138TasksQueuedDefault()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> task = alertCompactListCache.getOrFetch(new UserBaseKey(userId), null);
            task.execute();
        }
    }

    @Test
    public void checkNotCrashWhen139TasksQueuedDefault()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> task = alertCompactListCache.getOrFetch(new UserBaseKey(userId), null);
            task.execute();
        }
    }
}
