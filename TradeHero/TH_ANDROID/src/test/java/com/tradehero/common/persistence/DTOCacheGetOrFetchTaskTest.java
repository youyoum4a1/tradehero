package com.tradehero.common.persistence;

import android.os.AsyncTask;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.persistence.alert.AlertCompactListCacheRx;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

// TODO
@Ignore("This unit test depend on Environment resources")
@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DTOCacheGetOrFetchTaskTest
{
    @Inject AlertCompactListCacheRx alertCompactListCache;

    @Before public void setUp() throws Exception
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @After public void tearDown()
    {
        alertCompactListCache.invalidateAll();
        Robolectric.getBackgroundThreadScheduler().unPause();
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().unPause();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        ((ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR).getQueue().clear();
    }

    @Test
    public void checkNotCrashWhen138TasksQueuedPool()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            alertCompactListCache.get(new UserBaseKey(userId));
        }
    }

    @Test(expected = RejectedExecutionException.class)
    public void checkCrashWhen139TasksQueuedPool()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            alertCompactListCache.get(new UserBaseKey(userId));
        }
        assertTrue(false);
    }

    @Test
    public void checkNotCrashWhen138TasksQueuedDefault()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            alertCompactListCache.get(new UserBaseKey(userId));
        }
    }

    @Test
    public void checkNotCrashWhen139TasksQueuedDefault()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            alertCompactListCache.get(new UserBaseKey(userId));
        }
    }
}
