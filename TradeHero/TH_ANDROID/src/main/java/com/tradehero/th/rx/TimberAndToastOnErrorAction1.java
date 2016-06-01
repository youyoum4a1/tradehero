package com.ayondo.academy.rx;

import android.support.annotation.NonNull;
import timber.log.Timber;

public class TimberAndToastOnErrorAction1 extends ToastOnErrorAction1
{
    @NonNull private final String logMessage;

    public TimberAndToastOnErrorAction1(@NonNull String logMessage)
    {
        this.logMessage = logMessage;
    }

    public TimberAndToastOnErrorAction1(@NonNull String toastMessage, @NonNull String logMessage)
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
