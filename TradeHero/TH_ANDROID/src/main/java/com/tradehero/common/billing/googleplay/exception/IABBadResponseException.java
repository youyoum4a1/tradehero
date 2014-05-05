package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABBadResponseException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_BAD_RESPONSE;

    public IABBadResponseException(IABResult r)
    {
        super(r);
    }

    public IABBadResponseException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABBadResponseException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABBadResponseException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
