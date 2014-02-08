package com.tradehero.th.api.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 6:07 PM Copyright (c) TradeHero */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityCompactDTOTest
{
    public static final String EXT_KEY_EXCHANGE = "exchange";

    @Test public void shouldHaveHappySmiles() throws Exception
    {
        //String appName = new DashboardActivity().getResources().getString(R.string.app_name);
        //assertThat(appName, equalTo("TradeHero"));
        assertThat(1, equalTo(1));
    }
    //@Test
    //public void getExchangeLogoId_shouldThrowsNullPointerException() throws NullPointerException
    //{
    //    new SecurityCompactDTO().getExchangeLogoId();
    //}


}
