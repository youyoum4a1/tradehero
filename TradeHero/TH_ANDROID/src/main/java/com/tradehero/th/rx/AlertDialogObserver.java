package com.tradehero.th.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.misc.exception.KnownServerErrors;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.AlertDialogUtil;
import rx.Observer;

abstract public class AlertDialogObserver<T> implements Observer<T>
{
    @NonNull protected final Context activityContext;
    @NonNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    protected AlertDialogObserver(
            @NonNull Context activityContext,
            @NonNull AlertDialogUtil alertDialogUtil)
    {
        this.activityContext = activityContext;
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void onError(Throwable e)
    {
        THException reprocessed = new THException(e);
        String errorMessage = reprocessed.getMessage();
        if (errorMessage != null
                && (KnownServerErrors.isAccountAlreadyLinked(errorMessage)
                || KnownServerErrors.isAccountAlreadyRegistered(errorMessage)))
        {
            alertDialogUtil.popAccountAlreadyLinked(activityContext);
        }
        else
        {
            THToast.show(reprocessed);
        }
    }
}
