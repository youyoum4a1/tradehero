package com.tradehero.th.network.retrofit;

import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.ThroughDTOProcessor;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseMiddleCallback<ValueType> implements MiddleCallback<ValueType>
{
    private Callback<ValueType> primaryCallback;
    private DTOProcessor<ValueType> dtoProcessor;

    //<editor-fold desc="Constructors">
    public BaseMiddleCallback(Callback<ValueType> primaryCallback)
    {
        this(primaryCallback, new ThroughDTOProcessor<ValueType>());
    }

    public BaseMiddleCallback(Callback<ValueType> primaryCallback,
            DTOProcessor<ValueType> dtoProcessor)
    {
        super();
        this.primaryCallback = primaryCallback;
        this.dtoProcessor = dtoProcessor;
    }
    //</editor-fold>

    @Override public void setPrimaryCallback(Callback<ValueType> primaryCallback)
    {
        this.primaryCallback = primaryCallback;
    }

    @Override public void success(ValueType value, Response response)
    {
        notifySuccess(dtoProcessor.process(value), response);
    }

    protected void notifySuccess(ValueType value, Response response)
    {
        Callback<ValueType> primaryCallbackCopy = primaryCallback;
        if (primaryCallbackCopy != null)
        {
            primaryCallbackCopy.success(value, response);
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
