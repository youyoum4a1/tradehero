package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABUnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.UNHANDLED_DOMAIN;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
