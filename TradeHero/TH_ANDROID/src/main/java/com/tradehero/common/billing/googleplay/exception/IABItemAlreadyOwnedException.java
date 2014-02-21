package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABItemAlreadyOwnedException extends IABOneResponseValueException
{
    public static final String TAG = IABItemAlreadyOwnedException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;

    public IABItemAlreadyOwnedException(IABResult r)
    {
        super(r);
    }

    public IABItemAlreadyOwnedException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABItemAlreadyOwnedException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABItemAlreadyOwnedException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
