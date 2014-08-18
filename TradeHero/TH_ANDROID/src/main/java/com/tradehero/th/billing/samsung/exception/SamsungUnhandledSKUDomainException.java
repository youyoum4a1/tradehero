package com.tradehero.th.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.tradehero.th.billing.samsung.THSamsungConstants;

public class SamsungUnhandledSKUDomainException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.UNHANDLED_DOMAIN;

    //<editor-fold desc="Constructors">
    public SamsungUnhandledSKUDomainException()
    {
        super(VALID_RESPONSE);
    }

    public SamsungUnhandledSKUDomainException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public SamsungUnhandledSKUDomainException(String message, Throwable cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public SamsungUnhandledSKUDomainException(Throwable cause)
    {
        super(VALID_RESPONSE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
