package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABUserCancelledBillingException extends IABOneResponseValueException
{
    public static final String TAG = IABUserCancelledBillingException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_USER_CANCELED;

    //<editor-fold desc="Constructors">
    public IABUserCancelledBillingException(IABResult r)
    {
        super(r);
    }

    public IABUserCancelledBillingException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUserCancelledBillingException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUserCancelledBillingException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
