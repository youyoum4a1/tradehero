package com.tradehero.th.persistence.security;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityCompactListCacheTest
{
    private SecurityCompactListCache securityCompactListCache;

    @Before public void setUp()
    {
        this.securityCompactListCache = new SecurityCompactListCache(new Lazy<SecurityServiceWrapper>()
        {
            @Override public SecurityServiceWrapper get()
            {
                return null;
            }
        }, new Lazy<SecurityCompactCache>()
        {
            @Override public SecurityCompactCache get()
            {
                return null;
            }
        });
    }

    @After public void tearDown()
    {
        this.securityCompactListCache = null;
    }

    @Test public void basicAll_1_10NotEqualOthers()
    {
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 1, 10))).isNull();
        securityCompactListCache.put(new TrendingBasicSecurityListType(null, 1, 10), new SecurityIdList());
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 1, 10))).isNotNull();

        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 2, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 3, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 5, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 6, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 7, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 8, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 9, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 10, 10))).isNull();
    }

    @Test public void basicAll_4_10NotEqualOthers()
    {
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10))).isNull();
        securityCompactListCache.put(new TrendingBasicSecurityListType(null, 4, 10), new SecurityIdList());
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10))).isNotNull();

        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 5, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 6, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 7, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 8, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 9, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 10, 10))).isNull();
    }
}
