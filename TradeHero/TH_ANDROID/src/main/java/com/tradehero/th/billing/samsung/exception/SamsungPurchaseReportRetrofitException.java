package com.tradehero.th.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.tradehero.th.billing.samsung.THSamsungConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class SamsungPurchaseReportRetrofitException extends SamsungOneCodeException
{
    public static final String TAG = SamsungPurchaseReportRetrofitException.class.getSimpleName();
    public static final int VALID_RESPONSE = THSamsungConstants.PURCHASE_REPORT_RETROFIT_ERROR;

    public SamsungPurchaseReportRetrofitException()
    {
        super(VALID_RESPONSE);
    }

    public SamsungPurchaseReportRetrofitException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public SamsungPurchaseReportRetrofitException(String message, Throwable cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public SamsungPurchaseReportRetrofitException(Throwable cause)
    {
        super(VALID_RESPONSE, cause);
    }

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
