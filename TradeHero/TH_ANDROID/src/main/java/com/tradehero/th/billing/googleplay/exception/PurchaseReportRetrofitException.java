package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class PurchaseReportRetrofitException extends IABOneResponseValueException
{
    public static final String TAG = PurchaseReportRetrofitException.class.getSimpleName();
    public static final int VALID_RESPONSE = THIABConstants.PURCHASE_REPORT_RETROFIT_ERROR;

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
        super(cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
