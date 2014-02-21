package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class UnhandledSKUDomainException extends IABOneResponseValueException
{
    public static final String TAG = UnhandledSKUDomainException.class.getSimpleName();
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
