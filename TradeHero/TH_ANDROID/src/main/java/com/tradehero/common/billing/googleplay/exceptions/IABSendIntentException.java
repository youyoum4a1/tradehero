package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABSendIntentException extends IABOneResponseValueException
{
    public static final String TAG = IABSendIntentException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.IABHELPER_SEND_INTENT_FAILED;

    public IABSendIntentException(IABResult r)
    {
        super(r);
    }

    public IABSendIntentException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABSendIntentException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABSendIntentException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
