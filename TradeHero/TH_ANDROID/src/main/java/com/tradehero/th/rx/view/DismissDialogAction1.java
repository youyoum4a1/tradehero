package com.tradehero.th.rx.view;

import android.app.Dialog;
import android.support.annotation.Nullable;
import rx.functions.Action1;

public class DismissDialogAction1<T> implements Action1<T>
{
    @Nullable private final Dialog dialog;

    public DismissDialogAction1(@Nullable Dialog dialog)
    {
        this.dialog = dialog;
    }

    @Override public void call(T t)
    {
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
