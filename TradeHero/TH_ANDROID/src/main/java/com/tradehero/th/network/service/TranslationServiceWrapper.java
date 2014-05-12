package com.tradehero.th.network.service;

import com.tradehero.th.models.translation.TokenData;
import com.tradehero.th.models.translation.TranslationResult;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import timber.log.Timber;

@Singleton public class TranslationServiceWrapper
{
    private final TranslationTokenService translationTokenService;
    private final TranslationService translationService;
    private final TranslationServiceAsync translationServiceAsync;

    @Inject
    public TranslationServiceWrapper(
            TranslationTokenService translationTokenService,
            TranslationService translationService,
            TranslationServiceAsync translationServiceAsync)
    {
        this.translationTokenService = translationTokenService;
        this.translationService = translationService;
        this.translationServiceAsync = translationServiceAsync;
    }

    public TranslationResult translate(String from, String to, String text)
    {
        TokenData tokenData = translationTokenService.requestToken(
                NetworkConstants.TRANSLATION_REQ_TSCOPE,
                NetworkConstants.TRANSLATION_GRANT_TYPE,
                NetworkConstants.TRANSLATION_CLIENT_ID,
                NetworkConstants.TRANSLATION_CLIENT_SECRET
        );
        Timber.d("translate tokenData %s " + tokenData);
        if (tokenData == null || tokenData.getAccessToken() == null)
        {
            return null;
        }
        String contentType = "text/plain";
        return translationService.requestForTranslation("Bearer " + tokenData.getAccessToken(),
                        from, to, contentType, text);
    }

    public MiddleCallback<TranslationResult> translate(String from, String to, String text, Callback<TranslationResult> callback)
    {
        TokenData tokenData = translationTokenService.requestToken(
                NetworkConstants.TRANSLATION_REQ_TSCOPE,
                NetworkConstants.TRANSLATION_GRANT_TYPE,
                NetworkConstants.TRANSLATION_CLIENT_ID,
                NetworkConstants.TRANSLATION_CLIENT_SECRET
        );
        Timber.d("translate tokenData %s " + tokenData);
        if (tokenData == null || tokenData.getAccessToken() == null)
        {
            return null;
        }
        String contentType = "text/plain";
        MiddleCallback<TranslationResult> middleCallback = new BaseMiddleCallback<>(callback);
        translationServiceAsync.requestForTranslation("Bearer " + tokenData.getAccessToken(),
                        from, to, contentType, text, middleCallback);
        return middleCallback;
    }
}
