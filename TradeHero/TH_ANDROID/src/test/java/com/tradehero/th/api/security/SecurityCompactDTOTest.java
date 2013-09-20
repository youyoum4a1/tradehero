package com.tradehero.th.api.security;

import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.internal.runners.JUnit44RunnerImpl;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 6:07 PM Copyright (c) TradeHero */
@RunWith(RobolectricTestRunner.class)
public class SecurityCompactDTOTest
{
    @Test
    public void shouldHaveHappySmiles() throws Exception {
        //String appName = new DashboardActivity().getResources().getString(R.string.app_name);
        //assertThat(appName, equalTo("TradeHero"));
        assertThat(1, equalTo(1));
    }
    @Test
    public void getExchangeLogoId_shouldThrowsNullPointerException() throws NullPointerException
    {
        new SecurityCompactDTO().getExchangeLogoId();
    }
}
