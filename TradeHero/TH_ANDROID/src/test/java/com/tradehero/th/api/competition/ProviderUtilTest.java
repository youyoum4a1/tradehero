package com.tradehero.th.api.competition;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class ProviderUtilTest
{
    private static final String TEST_URL = "http://www.tradehero.mobi";

    @Inject ProviderUtil providerUtil;

    @Test public void shouldAppendToUrlCorrectly()
    {
        assertThat(providerUtil.appendToUrl(TEST_URL, "?test=1")).isEqualTo(TEST_URL + "?test=1");
        assertThat(providerUtil.appendToUrl(TEST_URL, "test=1")).isEqualTo(TEST_URL + "?test=1");
    }

    @Test public void shoudAppendUserIdCorrectly()
    {
        UserBaseKey userBaseKey = new UserBaseKey(10);
        assertThat(providerUtil.appendUserId(TEST_URL, '&', userBaseKey)).isEqualTo(TEST_URL + "?&userId=10");
        assertThat(providerUtil.appendUserId(TEST_URL, '?', userBaseKey)).isEqualTo(TEST_URL + "?userId=10");
    }

    @Test public void shoudAppendProviderIdCorrectly()
    {
        ProviderId providerId = new ProviderId(23);
        assertThat(providerUtil.appendProviderId(TEST_URL, '&', providerId)).isEqualTo(TEST_URL + "?&providerId=23");
        assertThat(providerUtil.appendProviderId(TEST_URL, '?', providerId)).isEqualTo(TEST_URL + "?providerId=23");
    }
}