package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;


public class IABUnknownErrorException extends IABOneResponseValueException
{
    public static final String TAG = IABUnknownErrorException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_UNKNOWN_ERROR;

    public IABUnknownErrorException(IABResult r)
    {
        super(r);
    }

    public IABUnknownErrorException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUnknownErrorException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUnknownErrorException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
