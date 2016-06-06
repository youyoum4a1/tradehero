package com.androidth.general.common.billing.samsung.exception;

import com.androidth.general.common.billing.samsung.SamsungConstants;

public class SamsungIOException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_IOEXCEPTION_ERROR;

    //<editor-fold desc="Constructors">
    public SamsungIOException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
