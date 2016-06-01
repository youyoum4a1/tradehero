package com.ayondo.academy.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABPurchaseReportRetrofitException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.PURCHASE_REPORT_RETROFIT_ERROR;

    //<editor-fold desc="Constructors">
    public IABPurchaseReportRetrofitException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABPurchaseReportRetrofitException(Throwable cause)
    {
        super(cause.getMessage(), cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
