package com.ayondo.academy.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.ayondo.academy.billing.samsung.THSamsungConstants;

public class SamsungUnhandledSKUDomainException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.UNHANDLED_DOMAIN;

    //<editor-fold desc="Constructors">
    public SamsungUnhandledSKUDomainException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
