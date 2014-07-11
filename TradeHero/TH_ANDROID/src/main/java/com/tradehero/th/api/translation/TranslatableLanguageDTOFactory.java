package com.tradehero.th.api.translation;

import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.i18n.LanguageDTOList;
import org.jetbrains.annotations.NotNull;

abstract public class TranslatableLanguageDTOFactory
{
    //<editor-fold desc="Constructors">
    protected TranslatableLanguageDTOFactory()
    {
    }
    //</editor-fold>

    @NotNull public LanguageDTOList getTargetLanguages()
    {
        LanguageDTOList targetLanguages = new LanguageDTOList();
        String[] langCodes = getLanguageCodes();
        for (String langCode : langCodes)
        {
            targetLanguages.add(new LanguageDTO(langCode));
        }
        return targetLanguages;
    }

    abstract protected String[] getLanguageCodes();

    @NotNull public LanguageDTO getBestMatch(@NotNull String languageCode, @NotNull String fallback)
    {
        LanguageDTO bestMatch = new LanguageDTO(fallback);
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
