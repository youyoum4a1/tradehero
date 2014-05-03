package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;


public class SamsungAlreadyPurchasedException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_ALREADY_PURCHASED;

    //<editor-fold desc="Constructors">
    public SamsungAlreadyPurchasedException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungAlreadyPurchasedException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungAlreadyPurchasedException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungAlreadyPurchasedException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
