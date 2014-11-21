package com.tradehero.th.network.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.ThroughDTOProcessor;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseMiddleCallback<ValueType>
    extends BaseCallbackWrapper<ValueType>
        implements MiddleCallback<ValueType>
{
    @NonNull protected final DTOProcessor<ValueType> dtoProcessor;

    //<editor-fold desc="Constructors">
    public BaseMiddleCallback(@Nullable Callback<ValueType> primaryCallback)
    {
        this(primaryCallback, new ThroughDTOProcessor<ValueType>());
    }

    public BaseMiddleCallback(
            @Nullable Callback<ValueType> primaryCallback,
            @NonNull DTOProcessor<ValueType> dtoProcessor)
    {
        super(primaryCallback);
        this.dtoProcessor = dtoProcessor;
    }
    //</editor-fold>

    @Override public void success(ValueType value, Response response)
    {
        notifySuccess(dtoProcessor.process(value), response);
    }

    @Override public void failure(RetrofitError error)
    {
        notifyFailure(error);
    }
}
