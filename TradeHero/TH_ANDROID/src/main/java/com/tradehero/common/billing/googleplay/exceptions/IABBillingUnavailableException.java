package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABBillingUnavailableException extends IABOneResponseValueException
{
    public static final String TAG = IABBillingUnavailableException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;

    public IABBillingUnavailableException(IABResult r)
    {
        super(r);
    }

    public IABBillingUnavailableException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABBillingUnavailableException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABBillingUnavailableException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
