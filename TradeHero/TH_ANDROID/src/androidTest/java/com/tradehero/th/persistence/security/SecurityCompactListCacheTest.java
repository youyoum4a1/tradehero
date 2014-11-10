package com.tradehero.th.persistence.security;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.base.TestTHApp;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
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
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 1, 10))).isNull();
        securityCompactListCache.onNext(new TrendingBasicSecurityListType(null, 1, 10), new SecurityCompactDTOList());
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
        securityCompactListCache.onNext(new TrendingBasicSecurityListType(null, 4, 10), new SecurityCompactDTOList());
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 4, 10))).isNotNull();

        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 5, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 6, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 7, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 8, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 9, 10))).isNull();
        assertThat(securityCompactListCache.get(new TrendingBasicSecurityListType(null, 10, 10))).isNull();
    }
}
