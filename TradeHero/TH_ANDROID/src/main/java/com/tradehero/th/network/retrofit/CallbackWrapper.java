package com.tradehero.th.network.retrofit;

import android.support.annotation.Nullable;
import retrofit.Callback;

public interface CallbackWrapper<ValueType>
{
    void setPrimaryCallback(@Nullable Callback<ValueType> primaryCallback);
}
