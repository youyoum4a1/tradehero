package com.tradehero.th.misc.callback;

import android.support.annotation.NonNull;
import com.tradehero.th.misc.exception.THException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class THCallback<T> implements Callback<T>
{
    @Override public void success(@NonNull T t, @NonNull Response response)
    {
        success(t, new THResponse(response));
        finish();
    }

    @Override public void failure(RetrofitError error)
    {
        failure(new THException(error));
        finish();
    }

    protected abstract void success(@NonNull T t, @NonNull THResponse thResponse);

    protected abstract void failure(THException ex);

    protected void finish()
    {
        // for override
    }
}
