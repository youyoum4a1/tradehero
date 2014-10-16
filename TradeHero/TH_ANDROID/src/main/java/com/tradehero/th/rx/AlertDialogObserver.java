package com.tradehero.th.rx;

import android.content.Context;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.misc.exception.KnownServerErrors;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.AlertDialogUtil;
import org.jetbrains.annotations.NotNull;
import rx.Observer;

abstract public class AlertDialogObserver<T> implements Observer<T>
{
    @NotNull protected final Context activityContext;
    @NotNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    protected AlertDialogObserver(
            @NotNull Context activityContext,
            @NotNull AlertDialogUtil alertDialogUtil)
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
