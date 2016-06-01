package com.ayondo.academy.api.competition;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.users.CurrentUserId;
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
public class ProviderUtilTest
{
    private static final String TEST_URL = "http://www.tradehero.mobi";

    @Inject ProviderUtil providerUtil;
    @Inject CurrentUserId currentUserId;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @After public void tearDown()
    {
        currentUserId.delete();
    }

    @Test public void shouldAppendToUrlCorrectly()
    {
        assertThat(providerUtil.appendToUrl(TEST_URL, "?test=1")).isEqualTo(TEST_URL + "?test=1");
        assertThat(providerUtil.appendToUrl(TEST_URL, "test=1")).isEqualTo(TEST_URL + "?test=1");
    }

    @Test public void shoudAppendUserIdCorrectly()
    {
        currentUserId.set(10);
        assertThat(providerUtil.appendUserId(TEST_URL, '&')).isEqualTo(TEST_URL + "?&userId=10");
        assertThat(providerUtil.appendUserId(TEST_URL, '?')).isEqualTo(TEST_URL + "?userId=10");
    }

    @Test public void shoudAppendProviderIdCorrectly()
    {
        ProviderId providerId = new ProviderId(23);
        assertThat(providerUtil.appendProviderId(TEST_URL, '&', providerId)).isEqualTo(TEST_URL + "?&providerId=23");
        assertThat(providerUtil.appendProviderId(TEST_URL, '?', providerId)).isEqualTo(TEST_URL + "?providerId=23");
    }
}