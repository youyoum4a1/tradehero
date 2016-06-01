package com.ayondo.academy.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.ayondo.academy.billing.samsung.THSamsungConstants;

public class SamsungPurchaseReportRetrofitException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.PURCHASE_REPORT_RETROFIT_ERROR;

    //<editor-fold desc="Constructors">
    public SamsungPurchaseReportRetrofitException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public SamsungPurchaseReportRetrofitException(Throwable cause)
    {
        super(VALID_RESPONSE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
