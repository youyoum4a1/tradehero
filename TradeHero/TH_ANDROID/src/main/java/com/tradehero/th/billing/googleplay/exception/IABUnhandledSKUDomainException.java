package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class IABUnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final String TAG = IABUnhandledSKUDomainException.class.getSimpleName();
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
