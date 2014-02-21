package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABItemUnavailableException extends IABOneResponseValueException
{
    public static final String TAG = IABItemUnavailableException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;

    //<editor-fold desc="Constructors">
    public IABItemUnavailableException(IABResult r)
    {
        super(r);
    }

    public IABItemUnavailableException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABItemUnavailableException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABItemUnavailableException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
