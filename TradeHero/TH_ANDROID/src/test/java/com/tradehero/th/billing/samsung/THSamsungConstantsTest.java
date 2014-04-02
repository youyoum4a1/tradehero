package com.tradehero.th.billing.samsung;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 2014/4/2.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class THSamsungConstantsTest
{
    @Test public void eyeBallTestTodayAsExpected()
    {
        assertEquals("20140402", THSamsungConstants.getTodayStringForInbox());
    }
}
