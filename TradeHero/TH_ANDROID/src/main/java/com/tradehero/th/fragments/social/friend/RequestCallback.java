package com.tradehero.th.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestCallback<T> implements Callback<T>
{
    @Nullable private ProgressDialog dialog;
    @NonNull private Context context;

    public RequestCallback(@NonNull Context context)
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

    @Override
    public void success(T data, Response response)
    {
        dismissDialog();
    }

    public void success()
    {
        dismissDialog();
    }

    @Override
    public void failure(RetrofitError retrofitError)
    {
        dismissDialog();
    }
}
