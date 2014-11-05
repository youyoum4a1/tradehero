package com.tradehero.th.api.translation;

import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.i18n.LanguageDTOFactory;
import com.tradehero.th.api.i18n.LanguageDTOList;
import android.support.annotation.NonNull;

/**
 * Given a translation service, not all languages known to Android may be translatable.
 */
abstract public class TranslatableLanguageDTOFactory
{
    @NonNull protected final LanguageDTOFactory languageDTOFactory;

    //<editor-fold desc="Constructors">
    protected TranslatableLanguageDTOFactory(@NonNull LanguageDTOFactory languageDTOFactory)
    {
        this.languageDTOFactory = languageDTOFactory;
    }
    //</editor-fold>

    @NonNull public LanguageDTOList getTargetLanguages()
    {
        LanguageDTOList targetLanguages = new LanguageDTOList();
        String[] langCodes = getLanguageCodes();
        for (String langCode : langCodes)
        {
            targetLanguages.add(languageDTOFactory.createFromCode(langCode));
        }
        return targetLanguages;
    }

    abstract protected String[] getLanguageCodes();

    @NonNull public LanguageDTO getBestMatch(@NonNull String languageCode, @NonNull String fallback)
    {
        LanguageDTO bestMatch = languageDTOFactory.createFromCode(fallback);
        for (LanguageDTO languageDTO : getTargetLanguages())
        {
            if (languageDTO.code.equals(languageCode))
            {
                bestMatch = languageDTO;
            }
        }
        return bestMatch;
    }
}
