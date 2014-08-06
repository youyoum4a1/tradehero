package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;

public class SamsungNeedUpgradeException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_NEED_APP_UPGRADE;

    //<editor-fold desc="Constructors">
    public SamsungNeedUpgradeException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungNeedUpgradeException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungNeedUpgradeException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungNeedUpgradeException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
