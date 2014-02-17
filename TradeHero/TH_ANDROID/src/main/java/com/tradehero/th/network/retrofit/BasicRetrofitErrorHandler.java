package com.tradehero.th.network.retrofit;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/2/13 Time: 5:38 PM To change this template use File | Settings | File Templates. */
public class BasicRetrofitErrorHandler
{
    public static void handle(RetrofitError retrofitError)
    {
        if (retrofitError.isNetworkError())
        {
            notifyNetworkError(retrofitError);
        }
    }

    public static void notifyNetworkError(RetrofitError retrofitError)
    {
        THToast.show(R.string.network_error);
    }
}
