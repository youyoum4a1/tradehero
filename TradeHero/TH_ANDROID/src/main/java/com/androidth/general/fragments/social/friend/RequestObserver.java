package com.androidth.general.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import rx.Observer;

public class RequestObserver<T> implements Observer<T>
{
    @Nullable private ProgressDialog dialog;
    @NonNull private Context context;

    public RequestObserver(@NonNull Context context)
    {
        this.context = context;
    }

    private void showDialog(@NonNull Context context)
    {
        if (dialog == null)
        {
            dialog = new ProgressDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.show();
    }

    private void dismissDialog()
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    public void onRequestStart()
    {
        showDialog(context);
    }

    @Override public void onNext(T t)
    {
        dismissDialog();
    }

    public void success()
    {
        dismissDialog();
    }

    @Override public void onError(Throwable e)
    {
        dismissDialog();
    }

    @Override public void onCompleted()
    {
    }
}
