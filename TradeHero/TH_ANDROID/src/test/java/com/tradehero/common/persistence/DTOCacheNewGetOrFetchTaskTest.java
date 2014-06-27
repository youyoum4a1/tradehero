package com.tradehero.common.persistence;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.social.HeroListCache;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertTrue;

//@RunWith(RobolectricMavenTestRunner.class)
public class DTOCacheNewGetOrFetchTaskTest
{
    @Inject HeroListCache heroListCache;

    @Before public void setUp() throws Exception
    {
        Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();
    }

    @After public void tearDown()
    {
        heroListCache.invalidateAll();
    }

    //@Test
    public void checkNotCrashWhen138TasksQueued()
    {
        for (int userId = 0; userId < 138; userId++)
        {
            heroListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
    }

    //@Test(expected = RejectedExecutionException.class)
    public void checkCrashWhen139TasksQueued()
    {
        for (int userId = 0; userId < 139; userId++)
        {
            heroListCache.getOrFetchAsync(new UserBaseKey(userId));
        }
        assertTrue(false);
    }
}
