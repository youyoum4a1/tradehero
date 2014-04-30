package com.tradehero.th.misc.callback;

import com.tradehero.th.misc.exception.THException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class THCallback<T> implements Callback<T>
{
    @Override public void success(T t, Response response)
    {
        success(t, new THResponse(response));
        finish();
    }

    @Override public void failure(RetrofitError error)
    {
        failure(new THException(error));
        finish();
    }

    protected abstract void success(T t, THResponse thResponse);

    protected abstract void failure(THException ex);

    protected void finish()
    {
        // for override
    }
}
