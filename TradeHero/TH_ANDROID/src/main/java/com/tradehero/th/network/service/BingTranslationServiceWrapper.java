package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.models.translation.bing.BaseMiddleCallbackBingTranslationResult;
import com.tradehero.th.network.retrofit.IntermediateCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class BingTranslationServiceWrapper
{
    private static final String PREFERRED_CONTENT_TYPE = "text/plain";

    private final BingTranslationService bingTranslationService;
    private final BingTranslationServiceAsync bingTranslationServiceAsync;

    @Inject public BingTranslationServiceWrapper(
            BingTranslationService bingTranslationService,
            BingTranslationServiceAsync bingTranslationServiceAsync)
    {
        this.bingTranslationService = bingTranslationService;
        this.bingTranslationServiceAsync = bingTranslationServiceAsync;
    }

    public BingTranslationResult translate(BingTranslationToken token, String from, String to, String text)
    {
        return bingTranslationService.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text);
    }

    public IntermediateCallback<TranslationResult> translate(BingTranslationToken token, String from, String to, String text,
            Callback<TranslationResult> callback)
    {
        BaseMiddleCallbackBingTranslationResult middleCallback = new BaseMiddleCallbackBingTranslationResult(callback);
        bingTranslationServiceAsync.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text,
                middleCallback);
        return middleCallback;
    }
}
