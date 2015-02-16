package com.tradehero.th.rx;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public class ToastAndLogOnErrorAction extends ToastOnErrorAction
{
    @NonNull private final String logMessage;

    public ToastAndLogOnErrorAction(@NonNull String logMessage)
    {
        this.logMessage = logMessage;
    }

    public ToastAndLogOnErrorAction(@NonNull String toastMessage, @NonNull String logMessage)
    {
        super(toastMessage);
        this.logMessage = logMessage;
    }

    @Override public void call(Throwable throwable)
    {
        Timber.e(throwable, logMessage);
        super.call(throwable);
    }
}
