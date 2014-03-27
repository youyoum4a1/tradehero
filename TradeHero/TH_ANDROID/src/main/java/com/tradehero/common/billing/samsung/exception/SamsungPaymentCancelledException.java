package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class SamsungPaymentCancelledException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_PAYMENT_IS_CANCELED;

    //<editor-fold desc="Constructors">
    public SamsungPaymentCancelledException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungPaymentCancelledException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungPaymentCancelledException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungPaymentCancelledException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
