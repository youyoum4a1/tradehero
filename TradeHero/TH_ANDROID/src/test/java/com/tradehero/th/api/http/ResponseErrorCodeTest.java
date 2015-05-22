package com.tradehero.th.api.http;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ResponseErrorCodeTest
{
    @Test public void unknownReturnsNull()
    {
        assertNull(ResponseErrorCode.getByCode(""));
    }

    @Test public void canGetOutdated()
    {
        assertEquals(
                ResponseErrorCode.OutDatedVersion,
                ResponseErrorCode.getByCode(ResponseErrorCode.OutDatedVersion.name()));
    }

    @Test public void canGetExpiredToken()
    {
        assertEquals(
                ResponseErrorCode.ExpiredSocialToken,
                ResponseErrorCode.getByCode(ResponseErrorCode.ExpiredSocialToken.name()));
    }
}