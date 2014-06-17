package com.tradehero.th.network.retrofit;

import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

public interface CallbackWrapper<ValueType>
{
    void setPrimaryCallback(@Nullable Callback<ValueType> primaryCallback);
}
