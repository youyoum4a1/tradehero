package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;

/**
 * Created by xavier on 2/21/14.
 */
public class IABRestorePurchaseMilestoneFailedException extends IABException
{
    public static final String TAG = IABRestorePurchaseMilestoneFailedException.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public IABRestorePurchaseMilestoneFailedException(IABResult r)
    {
        super(r);
    }

    public IABRestorePurchaseMilestoneFailedException(int response, String message)
    {
        super(response, message);
    }

    public IABRestorePurchaseMilestoneFailedException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABRestorePurchaseMilestoneFailedException(int response, String message, Exception cause)
    {
        super(response, message, cause);
    }

    public IABRestorePurchaseMilestoneFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IABRestorePurchaseMilestoneFailedException(Throwable cause)
    {
        super(cause);
    }
    //</editor-fold>
}
