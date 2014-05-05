package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

public class IABDeveloperErrorException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;

    //<editor-fold desc="Constructors">
    public IABDeveloperErrorException(IABResult r)
    {
        super(r);
    }

    public IABDeveloperErrorException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABDeveloperErrorException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABDeveloperErrorException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
