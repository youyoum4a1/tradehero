package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABUnknownPurchaseResponseException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE;

    public IABUnknownPurchaseResponseException(IABResult r)
    {
        super(r);
    }

    public IABUnknownPurchaseResponseException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUnknownPurchaseResponseException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUnknownPurchaseResponseException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
