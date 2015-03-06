package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class TranslationTokenServiceWrapper
{
    @NotNull private final TranslationTokenService translationTokenService;;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenServiceWrapper(
            @NotNull TranslationTokenService translationTokenService)
    {
        this.translationTokenService = translationTokenService;
    }
    //</editor-fold>

    //<editor-fold desc="Get Token">
    @NotNull public TranslationToken getToken()
    {
        return translationTokenService.requestToken();
    }
}
