package com.ayondo.academy.rx;

import rx.functions.Action1;
import timber.log.Timber;

public class TimberOnErrorAction1 implements Action1<Throwable>
{
    private final String message;

    public TimberOnErrorAction1(String message)
    {
        this.message = message;
    }

    @Override public void call(Throwable error)
    {
        Timber.e(error, message);
    }
}
