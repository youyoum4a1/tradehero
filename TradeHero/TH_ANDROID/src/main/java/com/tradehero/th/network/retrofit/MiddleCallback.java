package com.tradehero.th.network.retrofit;

import retrofit.Callback;

public interface MiddleCallback<ValueType> extends Callback<ValueType>
{
    void setPrimaryCallback(Callback<ValueType> primaryCallback);
}
