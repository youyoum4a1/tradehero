package com.androidth.general.common.billing.googleplay.exception;

import com.androidth.general.common.billing.googleplay.IABConstants;
import com.androidth.general.common.billing.googleplay.IABResult;

public class IABUserCancelledException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_USER_CANCELLED;

    //<editor-fold desc="Constructors">
    public IABUserCancelledException(IABResult r)
    {
        super(r);
    }

    public IABUserCancelledException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUserCancelledException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUserCancelledException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
