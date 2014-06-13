package com.tradehero.th.models.translation;

import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.TranslationTokenFactory;
import com.tradehero.th.models.DTOProcessor;

public class DTOProcessorGetTranslationToken implements DTOProcessor<TranslationToken>
{
    private final TranslationTokenFactory translationTokenFactory;

    public DTOProcessorGetTranslationToken(
            TranslationTokenFactory translationTokenFactory)
    {
        this.translationTokenFactory = translationTokenFactory;
    }

    @Override public TranslationToken process(TranslationToken value)
    {
        return translationTokenFactory.createSubClass(value);
    }
}
