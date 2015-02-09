package com.tradehero.th.rx;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import rx.functions.Action1;

public class ToastAction<T> implements Action1<T>
{
    @NonNull private final String text;

    //<editor-fold desc="Constructors">
    public ToastAction(@Nullable String text)
    {
        if (text == null)
        {
            this.text = "Error";
        }
        else
        {
            this.text = text;
        }
    }
    //</editor-fold>

    @Override public void call(T ignored)
    {
        THToast.show(text);
    }
}
