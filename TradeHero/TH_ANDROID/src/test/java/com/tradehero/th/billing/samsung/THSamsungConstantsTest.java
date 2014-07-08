package com.tradehero.th.billing.samsung;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class THSamsungConstantsTest
{
    @Ignore("Eyeball only")
    @Test public void eyeBallTestTodayAsExpected()
    {
        assertEquals("20140402", THSamsungConstants.getTodayStringForInbox());
    }
}
