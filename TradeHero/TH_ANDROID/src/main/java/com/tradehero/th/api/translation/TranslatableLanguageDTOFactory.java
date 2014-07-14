package com.tradehero.th.api.translation;

import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.i18n.LanguageDTOFactory;
import com.tradehero.th.api.i18n.LanguageDTOList;
import org.jetbrains.annotations.NotNull;

/**
 * Given a translation service, not all languages known to Android may be translatable.
 */
abstract public class TranslatableLanguageDTOFactory
{
    @NotNull protected final LanguageDTOFactory languageDTOFactory;

    //<editor-fold desc="Constructors">
    protected TranslatableLanguageDTOFactory(@NotNull LanguageDTOFactory languageDTOFactory)
    {
        this.languageDTOFactory = languageDTOFactory;
    }
    //</editor-fold>

    @NotNull public LanguageDTOList getTargetLanguages()
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

    @NotNull public LanguageDTO getBestMatch(@NotNull String languageCode, @NotNull String fallback)
    {
        LanguageDTO bestMatch = languageDTOFactory.createFromCode(fallback);
        for (@NotNull LanguageDTO languageDTO : getTargetLanguages())
        {
            if (languageDTO.code.equals(languageCode))
            {
                bestMatch = languageDTO;
            }
        }
        return bestMatch;
    }
}
