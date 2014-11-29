package com.tradehero.th.misc.callback;

import android.support.annotation.NonNull;
import retrofit.client.Response;

public class THResponse
{
    @NonNull private final Response response;

    public THResponse(@NonNull Response response)
    {
        this.response = response;
    }
}
