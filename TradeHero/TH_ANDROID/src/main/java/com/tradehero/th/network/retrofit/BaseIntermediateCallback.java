package com.tradehero.th.network.retrofit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseIntermediateCallback<ValueType>
    implements IntermediateCallback<ValueType>
{
    protected Callback<ValueType> primaryCallback;

    //<editor-fold desc="Constructors">
    public BaseIntermediateCallback(Callback<ValueType> primaryCallback)
    {
        this.primaryCallback = primaryCallback;
    }
    //</editor-fold>

    @Override public void setPrimaryCallback(Callback<ValueType> primaryCallback)
    {
        this.primaryCallback = primaryCallback;
    }

    protected void notifySuccess(ValueType value, Response response)
    {
        Callback<ValueType> primaryCallbackCopy = primaryCallback;
        if (primaryCallbackCopy != null)
        {
            primaryCallbackCopy.success(value, response);
        }
    }

    protected void notifyFailure(RetrofitError error)
    {
        Callback<ValueType> primaryCallbackCopy = primaryCallback;
        if (primaryCallbackCopy != null)
        {
            primaryCallbackCopy.failure(error);
        }
    }
}
