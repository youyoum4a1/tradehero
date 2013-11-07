package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABUnknownErrorException extends IABOneResponseValueException
{
    public static final String TAG = IABUnknownErrorException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.IABHELPER_UNKNOWN_ERROR;

    public IABUnknownErrorException(IABResult r)
    {
        super(r);
    }

    public IABUnknownErrorException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUnknownErrorException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUnknownErrorException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
