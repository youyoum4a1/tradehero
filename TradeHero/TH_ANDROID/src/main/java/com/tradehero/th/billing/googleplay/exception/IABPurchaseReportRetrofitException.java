package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class IABPurchaseReportRetrofitException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABConstants.PURCHASE_REPORT_RETROFIT_ERROR;

    public IABPurchaseReportRetrofitException(IABResult r)
    {
        super(r);
    }

    public IABPurchaseReportRetrofitException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABPurchaseReportRetrofitException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABPurchaseReportRetrofitException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public IABPurchaseReportRetrofitException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IABPurchaseReportRetrofitException(Throwable cause)
    {
        super(cause.getMessage(), cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
