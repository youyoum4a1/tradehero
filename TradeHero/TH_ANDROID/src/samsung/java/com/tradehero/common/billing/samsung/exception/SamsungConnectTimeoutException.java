package com.androidth.general.common.billing.samsung.exception;

import com.androidth.general.common.billing.samsung.SamsungConstants;

public class SamsungConnectTimeoutException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_CONNECT_TIMEOUT;

    //<editor-fold desc="Constructors">
    public SamsungConnectTimeoutException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
