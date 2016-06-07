package com.androidth.general.billing.googleplay.exception;

import com.androidth.general.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABMissingCachedProductDetailException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.MISSING_CACHED_DETAIL;

    //<editor-fold desc="Constructors">
    public IABMissingCachedProductDetailException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
