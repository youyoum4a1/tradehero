package com.tradehero.th.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestCallback<T> implements Callback<T>
{
    @Nullable private ProgressDialog dialog;
    @NotNull private Context context;

    public RequestCallback(@NotNull Context context)
    {
        this.context = context;
    }

    private void showDialog(@NotNull Context context)
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
