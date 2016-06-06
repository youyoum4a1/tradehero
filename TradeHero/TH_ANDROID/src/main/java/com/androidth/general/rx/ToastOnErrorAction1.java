package com.androidth.general.rx;

import android.support.annotation.Nullable;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.exception.THException;
import rx.functions.Action1;

public class ToastOnErrorAction1 implements Action1<Throwable>
{
    @Nullable private final String message;

    //<editor-fold desc="Constructors">
    public ToastOnErrorAction1()
    {
        this.message = null;
    }

    public ToastOnErrorAction1(@Nullable String message)
    {
        this.message = message;
    }
    //</editor-fold>

    @Override public void call(Throwable throwable)
    {
        if (message != null)
        {
            THToast.show(message);
        }
        else
        {
            THToast.show(new THException(throwable));
        }
    }
}
