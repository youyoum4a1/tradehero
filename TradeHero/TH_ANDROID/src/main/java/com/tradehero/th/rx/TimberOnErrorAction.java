package com.tradehero.th.rx;

import rx.functions.Action1;
import timber.log.Timber;

public class TimberOnErrorAction implements Action1<Throwable>
{
    private final String message;

    public TimberOnErrorAction(String message)
    {
        this.message = message;
    }

    @Override public void call(Throwable error)
    {
        Timber.e(error, message);
    }
}
