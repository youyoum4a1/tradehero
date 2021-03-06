package com.tradehero.th.api.http;

import com.tradehero.THRobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(THRobolectricTestRunner.class)
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
