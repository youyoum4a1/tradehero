package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;


public class SamsungWhileRunningException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_WHILE_RUNNING;

    //<editor-fold desc="Constructors">
    public SamsungWhileRunningException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungWhileRunningException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungWhileRunningException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungWhileRunningException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
