package com.tradehero.common.billing.samsung.exception;

import com.tradehero.common.billing.samsung.SamsungConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class SamsungNetworkUnavailableException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_NETWORK_NOT_AVAILABLE;

    //<editor-fold desc="Constructors">
    public SamsungNetworkUnavailableException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungNetworkUnavailableException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungNetworkUnavailableException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungNetworkUnavailableException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
