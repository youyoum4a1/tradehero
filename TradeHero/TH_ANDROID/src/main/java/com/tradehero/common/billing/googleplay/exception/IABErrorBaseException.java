package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABErrorBaseException extends IABOneResponseValueException
{
    public static final String TAG = IABErrorBaseException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_ERROR_BASE;

    public IABErrorBaseException(IABResult r)
    {
        super(r);
    }

    public IABErrorBaseException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABErrorBaseException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABErrorBaseException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
