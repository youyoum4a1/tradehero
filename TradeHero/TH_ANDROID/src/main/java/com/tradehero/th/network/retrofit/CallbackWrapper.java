package com.tradehero.th.network.retrofit;

import retrofit.Callback;

public interface CallbackWrapper<ValueType>
{
    void setPrimaryCallback(Callback<ValueType> primaryCallback);
}
