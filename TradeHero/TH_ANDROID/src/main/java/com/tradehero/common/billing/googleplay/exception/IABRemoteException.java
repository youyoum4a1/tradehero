package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;


public class IABRemoteException extends IABOneResponseValueException
{
    public static final String TAG = IABRemoteException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_REMOTE_EXCEPTION;

    public IABRemoteException(IABResult r)
    {
        super(r);
    }

    public IABRemoteException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABRemoteException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABRemoteException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
