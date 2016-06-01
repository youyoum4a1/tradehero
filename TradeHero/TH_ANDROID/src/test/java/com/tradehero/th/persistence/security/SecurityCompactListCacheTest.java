package com.ayondo.academy.persistence.security;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.key.TrendingBasicSecurityListType;
import com.ayondo.academy.base.TestTHApp;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SecurityCompactListCacheTest
{
    @Inject SecurityCompactListCacheRx securityCompactListCache;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @After public void tearDown()
    {
        this.securityCompactListCache = null;
    }

    @Test public void basicAll_1_10NotEqualOthers()
    {
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 1, 10)).toBlocking().firstOrDefault(null)).isNull();
        securityCompactListCache.onNext(new TrendingBasicSecurityListType(null, 1, 10), new SecurityCompactDTOList());
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 1, 10)).toBlocking().first()).isNotNull();

        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 2, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 3, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 5, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 6, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 7, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 8, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 9, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 10, 10)).toBlocking().firstOrDefault(null)).isNull();
    }

    @Test public void basicAll_4_10NotEqualOthers()
    {
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10)).toBlocking().firstOrDefault(null)).isNull();
        securityCompactListCache.onNext(new TrendingBasicSecurityListType(null, 4, 10), new SecurityCompactDTOList());
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10)).toBlocking().first()).isNotNull();

        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 5, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 6, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 7, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 8, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 9, 10)).toBlocking().firstOrDefault(null)).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 10, 10)).toBlocking().firstOrDefault(null)).isNull();
    }
}
