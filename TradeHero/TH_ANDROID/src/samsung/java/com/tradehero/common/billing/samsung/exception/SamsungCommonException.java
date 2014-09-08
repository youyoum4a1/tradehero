package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;

public class SamsungCommonException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_COMMON;

    //<editor-fold desc="Constructors">
    public SamsungCommonException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungCommonException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungCommonException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungCommonException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
