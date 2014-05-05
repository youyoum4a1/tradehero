package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABResultErrorException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_ERROR;

    //<editor-fold desc="Constructors">
    public IABResultErrorException(IABResult r)
    {
        super(r);
    }

    public IABResultErrorException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABResultErrorException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABResultErrorException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
