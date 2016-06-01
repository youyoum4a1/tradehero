package com.ayondo.academy.rx;

import rx.functions.Action1;
import timber.log.Timber;

public class TimberAction1<T> implements Action1<T>
{
    protected final String message;

    public TimberAction1(String message)
    {
        this.message = message;
    }

    @Override public void call(T object)
    {
        Timber.d(message);
    }
}
