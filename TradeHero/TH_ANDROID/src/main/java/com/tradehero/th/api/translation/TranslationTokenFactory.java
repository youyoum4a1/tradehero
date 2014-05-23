package com.tradehero.th.api.translation;

import com.tradehero.th.api.translation.bing.BingTranslationToken;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class TranslationTokenFactory
{
    @Inject public TranslationTokenFactory()
    {
        super();
    }

    public TranslationToken createSubClass(TranslationToken translationToken)
    {
        if (translationToken == null)
        {
            return null;
        }

        TranslationToken created;
        switch (translationToken.type)
        {
            case BingTranslationToken.TOKEN_TYPE:
                created = new BingTranslationToken(translationToken, BingTranslationToken.class);
                break;

            default:
                created = translationToken;
                Timber.w("Undetected TranslationToken type %s", translationToken);
        }
        return created;
    }
}
