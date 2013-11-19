package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABMissingTokenException extends IABOneResponseValueException
{
    public static final String TAG = IABMissingTokenException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.IABHELPER_MISSING_TOKEN;

    public IABMissingTokenException(IABResult r)
    {
        super(r);
    }

    public IABMissingTokenException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABMissingTokenException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABMissingTokenException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
