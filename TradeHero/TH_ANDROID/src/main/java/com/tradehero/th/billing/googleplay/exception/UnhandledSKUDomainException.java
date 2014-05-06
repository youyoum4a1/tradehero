package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class UnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABConstants.UNHANDLED_DOMAIN;

    public UnhandledSKUDomainException(IABResult r)
    {
        super(r);
    }

    public UnhandledSKUDomainException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public UnhandledSKUDomainException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public UnhandledSKUDomainException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
