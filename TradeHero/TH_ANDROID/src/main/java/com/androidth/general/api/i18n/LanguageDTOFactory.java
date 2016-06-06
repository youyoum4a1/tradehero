package com.androidth.general.api.i18n;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.i18n.lang.LanguageChineseSimplifiedDTO;
import com.androidth.general.api.i18n.lang.LanguageChineseTraditionalDTO;
import com.androidth.general.api.i18n.lang.LanguageHaitianCreoleDTO;
import com.androidth.general.api.i18n.lang.LanguageHebrewDTO;
import com.androidth.general.api.i18n.lang.LanguageHmongDawDTO;
import com.androidth.general.api.i18n.lang.LanguageIndonesianDTO;
import com.androidth.general.api.i18n.lang.LanguageKlingonDTO;
import com.androidth.general.api.i18n.lang.LanguageKlingonQaakDTO;
import com.androidth.general.api.i18n.lang.LanguageNorwegianDTO;
import java.util.Locale;

public class LanguageDTOFactory
{
    @NonNull public static LanguageDTO createFromCode(
            @NonNull Resources resources,
            @NonNull String languageCode)
    {
        Locale locale = forLanguageTag(languageCode);
        if (locale != null)
        {
            return new LanguageDTO(languageCode, locale);
        }
        LanguageDTO known = getHardCodedLanguages(resources).get(languageCode);
        if (known != null)
        {
            return known;
        }
        return new LanguageDTO(languageCode, Locale.ENGLISH);
    }

    @Nullable public static Locale forLanguageTag(@Nullable String languageCode)
    {
        for (Locale locale : Locale.getAvailableLocales())
        {
            if (locale.getLanguage().equals(languageCode))
            {
                return locale;
            }
        }
        return null;
    }

    @NonNull public static LanguageDTOMap getHardCodedLanguages(@NonNull Resources resources)
    {
        LanguageDTOMap known = new LanguageDTOMap();
        known.add(new LanguageHaitianCreoleDTO(resources));
        known.add(new LanguageHebrewDTO(resources));
        known.add(new LanguageHmongDawDTO(resources));
        known.add(new LanguageIndonesianDTO(resources));
        known.add(new LanguageKlingonDTO(resources));
        known.add(new LanguageKlingonQaakDTO(resources));
        known.add(new LanguageNorwegianDTO(resources));
        known.add(new LanguageChineseSimplifiedDTO(resources));
        known.add(new LanguageChineseTraditionalDTO(resources));
        return known;
    }
}
