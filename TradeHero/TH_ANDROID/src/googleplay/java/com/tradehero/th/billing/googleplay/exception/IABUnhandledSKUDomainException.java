package com.ayondo.academy.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABUnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.UNHANDLED_DOMAIN;

    //<editor-fold desc="Constructors">
    public IABUnhandledSKUDomainException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
