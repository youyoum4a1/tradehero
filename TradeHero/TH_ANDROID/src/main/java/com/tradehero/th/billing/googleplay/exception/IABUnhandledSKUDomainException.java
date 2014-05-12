package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class IABUnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABConstants.UNHANDLED_DOMAIN;

    public IABUnhandledSKUDomainException(IABResult r)
    {
        super(r);
    }

    public IABUnhandledSKUDomainException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUnhandledSKUDomainException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUnhandledSKUDomainException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
