package com.tradehero.common.persistence;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricMavenTestRunner.class)
public class PartialDTOCacheNewTest
{
    private TestStraightDTOCacheNew<UserBaseKey, ExpirableDTO> cache;

    @Before public void setUp()
    {
        cache = new TestStraightDTOCacheNew<UserBaseKey, ExpirableDTO>(10)
        {
            @NotNull @Override public ExpirableDTO fetch(@NotNull UserBaseKey key) throws Throwable
            {
                return new ExpirableDTO();
            }
        };
    }

    @Test
    public void testHurriedListenerCalledOnLaunchWhenCached()
    {
        UserBaseKey userBaseKey = new UserBaseKey(3);
        ExpirableDTO cached = new ExpirableDTO(3000);
        cache.put(userBaseKey, cached);

        DTOCacheNew.HurriedListener<UserBaseKey, ExpirableDTO> mockListener = mock(DTOCacheNew.HurriedListener.class);
        cache.register(userBaseKey, mockListener);
        cache.getOrFetchAsync(userBaseKey);

        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);
    }

    @Test
    public void testHurriedListenerCalledEvenWhenAlreadyLaunched()
    {
        UserBaseKey userBaseKey = new UserBaseKey(3);
        ExpirableDTO cached = new ExpirableDTO(3000);
        cache.put(userBaseKey, cached);

        DTOCacheNew.HurriedListener<UserBaseKey, ExpirableDTO> mockListener = mock(DTOCacheNew.HurriedListener.class);
        cache.getOrFetchAsync(userBaseKey);

        cache.register(userBaseKey, mockListener);

        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);
    }
}
