package com.tradehero.th.network.retrofit;

import retrofit.Callback;

public interface MiddleCallback<ValueType>
        extends CallbackWrapper<ValueType>, Callback<ValueType>
{
}
