package com.tradehero.common.billing.samsung.exception;

import com.tradehero.common.billing.samsung.SamsungConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class SamsungConnectTimeoutException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_CONNECT_TIMEOUT;

    //<editor-fold desc="Constructors">
    public SamsungConnectTimeoutException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungConnectTimeoutException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungConnectTimeoutException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungConnectTimeoutException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
