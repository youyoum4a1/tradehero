package com.tradehero.th.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.tradehero.th.billing.samsung.THSamsungConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class SamsungUnhandledSKUDomainException extends SamsungOneCodeException
{
    public static final String TAG = SamsungUnhandledSKUDomainException.class.getSimpleName();
    public static final int VALID_RESPONSE = THSamsungConstants.UNHANDLED_DOMAIN;

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

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
