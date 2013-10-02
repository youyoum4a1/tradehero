package com.tradehero.th.network;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA.
 * User: xavier
 * Date: 8/29/13
 * Time: 10:58 AM
 */
public abstract class CallbackWithSpecificNotifiers<T> implements Callback<T>
{
    @Override public void success(T returned, Response response)
    {
        notifyIsQuerying(false);
    }

    @Override public void failure(RetrofitError retrofitError)
    {
        notifyIsQuerying(false);
        BasicRetrofitErrorHandler.handle(retrofitError);
    }

    public abstract void notifyIsQuerying(boolean isQuerying);
}
