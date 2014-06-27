package com.tradehero.common.persistence;

import android.os.AsyncTask;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertTrue;

//@RunWith(RobolectricMavenTestRunner.class)
public class DTOCacheGetOrFetchTaskTest
{
    @Inject AlertCompactListCache alertCompactListCache;

    @Before public void setUp() throws Exception
    {
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

    //@Test
    public void checkNotCrashWhen138TasksQueuedPool()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            alertCompactListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
    }

    //@Test(expected = RejectedExecutionException.class)
    public void checkCrashWhen139TasksQueuedPool()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            alertCompactListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
        assertTrue(false);
    }

    //@Test
    public void checkNotCrashWhen138TasksQueuedDefault()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            alertCompactListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
    }

    //@Test
    public void checkNotCrashWhen139TasksQueuedDefault()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            alertCompactListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
    }
}
