package com.ayondo.academy.rx.view;

import android.app.Dialog;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import rx.functions.Action0;

public class DismissDialogAction0 implements Action0
{
    @Nullable private final Dialog dialog;

    public DismissDialogAction0(@Nullable Dialog dialog)
    {
        this.dialog = dialog;
    }

    @CallSuper
    @Override public void call()
    {
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
