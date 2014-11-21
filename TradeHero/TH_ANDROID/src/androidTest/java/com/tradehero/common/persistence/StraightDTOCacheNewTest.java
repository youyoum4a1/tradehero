package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(THRobolectricTestRunner.class)
public class StraightDTOCacheNewTest
{
    @Inject DTOCacheUtilNew dtoCacheUtil;

    private TestStraightDTOCacheNew<UserBaseKey, ExpirableDTO> cache;

    @Before public void setUp()
    {
        cache = new TestStraightDTOCacheNew<UserBaseKey, ExpirableDTO>(10, dtoCacheUtil)
        {
            @NonNull @Override public ExpirableDTO fetch(@NonNull UserBaseKey key) throws Throwable
            {
                return new ExpirableDTO();
            }
        };
    }

    private DTOCacheNew.Listener<UserBaseKey, ExpirableDTO> createEmptyListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, ExpirableDTO>(){
            @Override public void onDTOReceived(@NonNull UserBaseKey key, @NonNull ExpirableDTO value)
            {
            }

            @Override public void onErrorThrown(@NonNull UserBaseKey key, @NonNull Throwable error)
            {
            }
        };
    }

    //<editor-fold desc="Assertions">
    private void assertThatHasCacheValue(UserBaseKey key)
    {
        assertThat(cache.isCacheValueNull(key)).isFalse();
    }

    private void assertThatHasNoCacheValue(UserBaseKey key)
    {
        assertThat(cache.isCacheValueNull(key)).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Test Get">
    @Test public void testGetValidReturnsValue()
    {
        UserBaseKey key = new UserBaseKey(1);
        ExpirableDTO expirableDTO = new ExpirableDTO(1000);
        cache.put(key, expirableDTO);
        assertThat(cache.get(key)).isEqualTo(expirableDTO);
    }

    @Test public void testGetInvalidReturnsZero()
    {
        UserBaseKey key = new UserBaseKey(1);
        cache.put(key, new ExpirableDTO());
        assertThat(cache.get(key)).isNull();
    }
    //</editor-fold>

    //<editor-fold desc="Test Put">
    @Test public void testPutInvalidValueDoesNot()
    {
        UserBaseKey key = new UserBaseKey(1);
        assertThatHasNoCacheValue(key);
        cache.put(key, new ExpirableDTO());
        assertThatHasNoCacheValue(key);
    }

    @Test public void testPutValidValueDoesIt()
    {
        UserBaseKey key = new UserBaseKey(1);
        assertThatHasNoCacheValue(key);
        cache.put(key, new ExpirableDTO(1000));
        assertThatHasCacheValue(key);
    }
    //</editor-fold>

    //<editor-fold desc="Test Get When Invalid">
    @Ignore("Don't do thread.sleep")
    @Test public void testGetInvalidWithNoListenerThenClears() throws InterruptedException
    {
        UserBaseKey key = new UserBaseKey(1);
        assertThatHasNoCacheValue(key);
        cache.put(key, new ExpirableDTO(1));

        assertThatHasCacheValue(key);
        Thread.sleep(1000);
        assertThatHasCacheValue(key);

        // Now trigger the invalidation
        assertThat(cache.get(key)).isNull();
        assertThatHasNoCacheValue(key);
    }

    @Ignore("Don't do thread.sleep")
    @Test public void testGetInvalidWithListenerThenDoesNotClear() throws InterruptedException
    {
        UserBaseKey key = new UserBaseKey(1);
        assertThatHasNoCacheValue(key);
        cache.put(key, new ExpirableDTO(1));
        cache.register(key, createEmptyListener());

        assertThatHasCacheValue(key);
        Thread.sleep(1200);
        assertThatHasCacheValue(key);

        // Now trigger the invalidation
        assertThat(cache.get(key)).isNull();
        assertThatHasCacheValue(key);
        assertThat(cache.getListenersCount(key)).isEqualTo(1);
    }
    //</editor-fold>
}
