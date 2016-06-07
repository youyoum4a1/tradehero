package com.androidth.general.common.billing.googleplay.exception;

import com.androidth.general.common.billing.googleplay.IABConstants;

public class IABBadResponseException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_BAD_RESPONSE;

    //<editor-fold desc="Constructors">
    //public IABBadResponseException(IABResult r)
    //{
    //    super(r);
    //}

    //public IABBadResponseException(IABResult r, Exception cause)
    //{
    //    super(r, cause);
    //}

    public IABBadResponseException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABBadResponseException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
