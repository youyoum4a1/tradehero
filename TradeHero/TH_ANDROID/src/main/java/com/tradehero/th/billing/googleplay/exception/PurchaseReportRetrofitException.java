package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class PurchaseReportRetrofitException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.PURCHASE_REPORT_RETROFIT_ERROR;

    public PurchaseReportRetrofitException(IABResult r)
    {
        super(r);
    }

    public PurchaseReportRetrofitException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public PurchaseReportRetrofitException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public PurchaseReportRetrofitException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public PurchaseReportRetrofitException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PurchaseReportRetrofitException(Throwable cause)
    {
        super(cause.getMessage(), cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
