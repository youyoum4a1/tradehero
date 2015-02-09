package com.tradehero.th.rx;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.misc.exception.THException;
import rx.functions.Action1;
import timber.log.Timber;

public class ToastOnErrorAction implements Action1<Throwable>
{
    //<editor-fold desc="Constructors">
    public ToastOnErrorAction()
    {
    }
    //</editor-fold>

    @Override public void call(Throwable throwable)
    {
        Timber.d(throwable, "Error");
        THToast.show(new THException(throwable));
    }
}
