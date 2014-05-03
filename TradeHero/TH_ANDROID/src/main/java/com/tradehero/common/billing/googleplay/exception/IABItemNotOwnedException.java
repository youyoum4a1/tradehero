package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;


public class IABItemNotOwnedException extends IABOneResponseValueException
{
    public static final String TAG = IABItemNotOwnedException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED;

    //<editor-fold desc="Constructors">
    public IABItemNotOwnedException(IABResult r)
    {
        super(r);
    }

    public IABItemNotOwnedException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABItemNotOwnedException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABItemNotOwnedException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
