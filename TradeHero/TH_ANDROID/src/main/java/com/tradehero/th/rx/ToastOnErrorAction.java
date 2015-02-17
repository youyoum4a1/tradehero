package com.tradehero.th.rx;

import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.misc.exception.THException;
import rx.functions.Action1;

public class ToastOnErrorAction implements Action1<Throwable>
{
    @Nullable private final String message;

    //<editor-fold desc="Constructors">
    public ToastOnErrorAction()
    {
        this.message = null;
    }

    public ToastOnErrorAction(@Nullable String message)
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
