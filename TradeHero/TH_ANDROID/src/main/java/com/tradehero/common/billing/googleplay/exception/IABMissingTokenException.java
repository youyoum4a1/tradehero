package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;


public class IABMissingTokenException extends IABOneResponseValueException
{
    public static final String TAG = IABMissingTokenException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_MISSING_TOKEN;

    public IABMissingTokenException(IABResult r)
    {
        super(r);
    }

    public IABMissingTokenException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABMissingTokenException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABMissingTokenException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
