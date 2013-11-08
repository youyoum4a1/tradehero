package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABAlreadyOwnedException extends IABOneResponseValueException
{
    public static final String TAG = IABAlreadyOwnedException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;

    public IABAlreadyOwnedException(IABResult r)
    {
        super(r);
    }

    public IABAlreadyOwnedException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABAlreadyOwnedException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABAlreadyOwnedException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
