package com.tradehero.th.persistence.security;

import android.content.SharedPreferences;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by xavier on 12/16/13.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class SecurityCompactListCacheTest
{
    public static final String TAG = SecurityCompactListCacheTest.class.getSimpleName();

    private SecurityCompactListCache securityCompactListCache;

    @Before public void setUp()
    {
        this.securityCompactListCache = new SecurityCompactListCache();
    }

    @After public void tearDown()
    {
        this.securityCompactListCache = null;
    }

    @Test public void basicAll_1_10NotEqualOthers()
    {
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 1, 10)));
        securityCompactListCache.put(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 1, 10), new SecurityIdList());
        assertNotNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 1, 10)));

        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 2, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 3, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 4, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 5, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 6, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 7, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 8, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 9, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 10, 10)));
    }

    @Test public void basicAll_4_10NotEqualOthers()
    {
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 4, 10)));
        securityCompactListCache.put(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 4, 10), new SecurityIdList());
        assertNotNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 4, 10)));

        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 5, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 6, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 7, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 8, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 9, 10)));
        assertNull(securityCompactListCache.get(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 10, 10)));
    }
}
