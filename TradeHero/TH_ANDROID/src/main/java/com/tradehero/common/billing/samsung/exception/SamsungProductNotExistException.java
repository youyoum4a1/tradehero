package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;


public class SamsungProductNotExistException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_PRODUCT_DOES_NOT_EXIST;

    //<editor-fold desc="Constructors">
    public SamsungProductNotExistException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungProductNotExistException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungProductNotExistException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungProductNotExistException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
