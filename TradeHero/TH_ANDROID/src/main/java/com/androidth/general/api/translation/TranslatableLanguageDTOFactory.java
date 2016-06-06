package com.androidth.general.api.translation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.api.i18n.LanguageDTO;
import com.androidth.general.api.i18n.LanguageDTOFactory;
import com.androidth.general.api.i18n.LanguageDTOList;

/**
 * Given a translation service, not all languages known to Android may be translatable.
 */
abstract public class TranslatableLanguageDTOFactory
{
    @NonNull public LanguageDTOList getTargetLanguages(@NonNull Resources resources)
    {
        LanguageDTOList targetLanguages = new LanguageDTOList();
        String[] langCodes = getLanguageCodes(resources);
        for (String langCode : langCodes)
        {
            targetLanguages.add(LanguageDTOFactory.createFromCode(resources, langCode));
        }
        return targetLanguages;
    }

    abstract protected String[] getLanguageCodes(@NonNull Resources resources);

    @NonNull public LanguageDTO getBestMatch(
            @NonNull Resources resources,
            @NonNull String languageCode,
            @NonNull String fallback)
    {
        LanguageDTO bestMatch = LanguageDTOFactory.createFromCode(resources, fallback);
        for (LanguageDTO languageDTO : getTargetLanguages(resources))
        {
            if (languageDTO.code.equals(languageCode))
            {
                bestMatch = languageDTO;
            }
        }
        return bestMatch;
    }
}
