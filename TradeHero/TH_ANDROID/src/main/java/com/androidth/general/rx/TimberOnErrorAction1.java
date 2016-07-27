package com.androidth.general.rx;

import android.content.Context;
import android.widget.Toast;

import rx.functions.Action1;
import timber.log.Timber;

public class TimberOnErrorAction1 implements Action1<Throwable>
{
    private final String message;

    public TimberOnErrorAction1(String message)
    {
        this.message = message;
    }

    public TimberOnErrorAction1(String message, Context context)
    {
        this.message = message;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override public void call(Throwable error)
    {
        Timber.e(error, message);
    }
}
