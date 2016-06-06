package com.androidth.general.common.billing.samsung.exception;

import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;

public class SamsungCommonException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_COMMON;

    //<editor-fold desc="Constructors">
    public SamsungCommonException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
