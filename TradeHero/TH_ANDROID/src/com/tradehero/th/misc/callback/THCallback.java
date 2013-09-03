package com.tradehero.th.misc.callback;

import com.tradehero.th.misc.exception.THException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 5:18 PM Copyright (c) TradeHero */
public abstract class THCallback<T> implements Callback<T>
{
    @Override public void success(T t, Response response)
    {
        finish();
        success(t, new THResponse(response));
    }

    @Override public void failure(RetrofitError error)
    {
        finish();
        failure(new THException(error));
    }

    protected abstract void success(T t, THResponse thResponse);

    protected abstract void failure(THException ex);

    protected void finish()
    {
        // for override
    }
}
