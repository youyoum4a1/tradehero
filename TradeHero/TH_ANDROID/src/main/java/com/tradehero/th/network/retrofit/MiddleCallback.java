package com.tradehero.th.network.retrofit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
public class MiddleCallback<ValueType> implements Callback<ValueType>
{
    public static final String TAG = MiddleCallback.class.getSimpleName();

    private Callback<ValueType> primaryCallback;

    public MiddleCallback(Callback<ValueType> primaryCallback)
    {
        super();
        this.primaryCallback = primaryCallback;
    }

    public void setPrimaryCallback(Callback<ValueType> primaryCallback)
    {
        this.primaryCallback = primaryCallback;
    }

    @Override public void success(ValueType valueType, Response response)
    {
        notifySuccess(valueType, response);
    }

    protected void notifySuccess(ValueType valueType, Response response)
    {
        Callback<ValueType> primaryCallbackCopy = primaryCallback;
        if (primaryCallbackCopy != null)
        {
            primaryCallbackCopy.success(valueType, response);
        }
    }

    @Override public void failure(RetrofitError error)
    {
        notifyFailure(error);
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
