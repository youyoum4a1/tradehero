package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FacebookRequestCallback extends RequestCallback<BaseResponseDTO>
{
    public FacebookRequestCallback(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public final void success(BaseResponseDTO data, Response response)
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
