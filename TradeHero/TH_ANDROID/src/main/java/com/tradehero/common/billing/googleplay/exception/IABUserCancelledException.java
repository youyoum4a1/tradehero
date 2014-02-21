package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResult;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABUserCancelledException extends IABOneResponseValueException
{
    public static final String TAG = IABUserCancelledException.class.getSimpleName();
    public static final int VALID_RESPONSE = IABConstants.IABHELPER_USER_CANCELLED;

    //<editor-fold desc="Constructors">
    public IABUserCancelledException(IABResult r)
    {
        super(r);
    }

    public IABUserCancelledException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABUserCancelledException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABUserCancelledException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
