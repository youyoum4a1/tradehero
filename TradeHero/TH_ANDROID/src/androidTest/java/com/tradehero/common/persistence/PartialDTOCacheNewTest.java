package com.tradehero.common.persistence;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(THRobolectricTestRunner.class)
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

        //noinspection unchecked
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

        //noinspection unchecked
        DTOCacheNew.HurriedListener<UserBaseKey, ExpirableDTO> mockListener = mock(DTOCacheNew.HurriedListener.class);
        cache.getOrFetchAsync(userBaseKey);

        cache.register(userBaseKey, mockListener);
        verify(mockListener, times(0)).onPreCachedDTOReceived(userBaseKey, cached);

        cache.getOrFetchAsync(userBaseKey);
        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);
    }

    @Test
    public void testHurriedListenerNotCalledAgainWhenCalledOnce()
    {
        UserBaseKey userBaseKey = new UserBaseKey(3);
        ExpirableDTO cached = new ExpirableDTO(3000);
        cache.put(userBaseKey, cached);

        //noinspection unchecked
        DTOCacheNew.HurriedListener<UserBaseKey, ExpirableDTO> mockListener = mock(DTOCacheNew.HurriedListener.class);
        cache.getOrFetchAsync(userBaseKey);

        cache.register(userBaseKey, mockListener);

        cache.getOrFetchAsync(userBaseKey);
        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);

        cache.getOrFetchAsync(userBaseKey);
        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);

    }

    @Test
    public void testHurriedListenerCanRegisterAgainWithinOnPreReceived()
    {
        final UserBaseKey userBaseKey = new UserBaseKey(3);
        ExpirableDTO cached = new ExpirableDTO(3000);
        cache.put(userBaseKey, cached);

        //noinspection unchecked
        final DTOCacheNew.HurriedListener<UserBaseKey, ExpirableDTO> mockListener = mock(DTOCacheNew.HurriedListener.class);
        doAnswer(new Answer()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                cache.register(userBaseKey, mockListener);
                return null;
            }
        }).when(mockListener).onPreCachedDTOReceived(userBaseKey, cached);

        cache.register(userBaseKey, mockListener);
        cache.getOrFetchAsync(userBaseKey);
        verify(mockListener, times(1)).onPreCachedDTOReceived(userBaseKey, cached);

        cache.getOrFetchAsync(userBaseKey);
        verify(mockListener, times(2)).onPreCachedDTOReceived(userBaseKey, cached);

        cache.unregister(mockListener);
    }
}
