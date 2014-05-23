package com.tradehero.th.network.retrofit;

import retrofit.Callback;

public interface IntermediateCallback<ValueType>
{
    void setPrimaryCallback(Callback<ValueType> primaryCallback);
}
