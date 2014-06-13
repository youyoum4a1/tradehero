package com.tradehero.th.models.translation.bing;

import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationResult;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.network.retrofit.BaseCallbackWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseMiddleCallbackBingTranslationResult
    extends BaseCallbackWrapper<TranslationResult>
    implements Callback<BingTranslationResult>
{
    protected DTOProcessor<BingTranslationResult> dtoProcessor;

    //<editor-fold desc="Constructors">
    public BaseMiddleCallbackBingTranslationResult(Callback<TranslationResult> primaryCallback)
    {
        this(primaryCallback, new ThroughDTOProcessor<BingTranslationResult>());
    }

    public BaseMiddleCallbackBingTranslationResult(
            Callback<TranslationResult> primaryCallback,
            DTOProcessor<BingTranslationResult> dtoProcessor)
    {
        super(primaryCallback);
        this.dtoProcessor = dtoProcessor;
    }
    //</editor-fold>

    @Override public void success(BingTranslationResult value, Response response)
    {
        notifySuccess(dtoProcessor.process(value), response);
    }

    @Override public void failure(RetrofitError error)
    {
        notifyFailure(error);
    }
}
