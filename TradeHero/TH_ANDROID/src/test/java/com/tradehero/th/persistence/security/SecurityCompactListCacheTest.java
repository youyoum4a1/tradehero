package com.tradehero.th.persistence.security;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityCompactListCacheTest
{
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
