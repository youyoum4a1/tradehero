package com.androidth.general.billing.googleplay.exception;

import com.androidth.general.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABInvalidQuantityException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.INVALID_QUANTITY;

    //<editor-fold desc="Constructors">
    public IABInvalidQuantityException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
