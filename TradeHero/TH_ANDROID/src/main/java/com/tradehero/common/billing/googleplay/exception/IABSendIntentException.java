package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABSendIntentException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_SEND_INTENT_FAILED;

    public IABSendIntentException(IABResult r)
    {
        super(r);
    }

    public IABSendIntentException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABSendIntentException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABSendIntentException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
