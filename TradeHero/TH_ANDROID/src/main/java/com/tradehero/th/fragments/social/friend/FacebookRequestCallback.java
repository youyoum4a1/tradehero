package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FacebookRequestCallback extends RequestCallback
{
    public FacebookRequestCallback(@NotNull Context context)
    {
        super(context);
    }

    @Override
    public final void success(Object data, Response response)
    {
        this.success();
    }

    public void success()
    {
    }

    public void failure()
    {
    }

    @Override
    public final void failure(RetrofitError retrofitError)
    {
        this.failure();
    }
}
