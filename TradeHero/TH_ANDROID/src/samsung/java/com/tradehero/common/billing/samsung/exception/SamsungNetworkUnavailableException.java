package com.androidth.general.common.billing.samsung.exception;

import com.androidth.general.common.billing.samsung.SamsungConstants;

public class SamsungNetworkUnavailableException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_NETWORK_NOT_AVAILABLE;

    //<editor-fold desc="Constructors">
    public SamsungNetworkUnavailableException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
