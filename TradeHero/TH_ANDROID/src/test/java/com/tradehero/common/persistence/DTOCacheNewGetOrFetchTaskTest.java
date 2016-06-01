package com.tradehero.common.persistence;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.persistence.social.HeroListCacheRx;
import java.util.concurrent.RejectedExecutionException;
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
public class DTOCacheNewGetOrFetchTaskTest
{
    @Inject HeroListCacheRx heroListCache;

    @Before public void setUp() throws Exception
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @After public void tearDown()
    {
        heroListCache.invalidateAll();
    }

    @Test
    public void checkNotCrashWhen138TasksQueued()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            heroListCache.get(new UserBaseKey(userId));
        }
    }

    @Test(expected = RejectedExecutionException.class)
    public void checkCrashWhen139TasksQueued()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            heroListCache.get(new UserBaseKey(userId));
        }
        assertTrue(false);
    }
}
