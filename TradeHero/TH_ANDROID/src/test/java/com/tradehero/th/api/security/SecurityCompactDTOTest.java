package com.tradehero.th.api.security;

import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 6:07 PM Copyright (c) TradeHero */
@RunWith(RobolectricTestRunner.class)
public class SecurityCompactDTOTest
{
    @Test
    public void shouldHaveHappySmiles() throws Exception {
        String appName = new DashboardActivity().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("TradeHero"));
    }
    //@Test
    //public void getExchangeLogoId_shouldThrowsNullPointerException() throws NullPointerException
    //{
    //    new SecurityCompactDTO().getExchangeLogoId();
    //}
}
