package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABSubscriptionUnavailableException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE;

    public IABSubscriptionUnavailableException(IABResult r)
    {
        super(r);
    }

    public IABSubscriptionUnavailableException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABSubscriptionUnavailableException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABSubscriptionUnavailableException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
