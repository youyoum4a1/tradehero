package com.tradehero.th.api.i18n;

import android.content.Context;

import com.tradehero.th.api.i18n.lang.LanguageChineseSimplifiedDTO;
import com.tradehero.th.api.i18n.lang.LanguageChineseTraditionalDTO;
import com.tradehero.th.api.i18n.lang.LanguageHaitianCreoleDTO;
import com.tradehero.th.api.i18n.lang.LanguageHebrewDTO;
import com.tradehero.th.api.i18n.lang.LanguageHmongDawDTO;
import com.tradehero.th.api.i18n.lang.LanguageIndonesianDTO;
import com.tradehero.th.api.i18n.lang.LanguageKlingonDTO;
import com.tradehero.th.api.i18n.lang.LanguageKlingonQaakDTO;
import com.tradehero.th.api.i18n.lang.LanguageNorwegianDTO;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import javax.inject.Inject;

public class LanguageDTOFactory
{
    @NonNull private final Context applicationContext;

    //<editor-fold desc="Constructors">
    @Inject public LanguageDTOFactory(@NonNull Context applicationContext)
    {
        this.applicationContext = applicationContext;
    }
    //</editor-fold>

    public LanguageDTO createFromCode(@NonNull String languageCode)
    {
        Locale locale = forLanguageTag(languageCode);
        if (locale != null)
        {
            return new LanguageDTO(languageCode, locale);
        }
        LanguageDTO known = getHardCodedLanguages().get(languageCode);
        if (known != null)
        {
            return known;
        }
        return new LanguageDTO(languageCode, Locale.ENGLISH);
    }

    @Nullable public Locale forLanguageTag(String languageCode)
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

    @NonNull public LanguageDTOMap getHardCodedLanguages()
    {
        LanguageDTOMap known = new LanguageDTOMap();
        known.add(new LanguageHaitianCreoleDTO(applicationContext.getResources()));
        known.add(new LanguageHebrewDTO(applicationContext.getResources()));
        known.add(new LanguageHmongDawDTO(applicationContext.getResources()));
        known.add(new LanguageIndonesianDTO(applicationContext.getResources()));
        known.add(new LanguageKlingonDTO(applicationContext.getResources()));
        known.add(new LanguageKlingonQaakDTO(applicationContext.getResources()));
        known.add(new LanguageNorwegianDTO(applicationContext.getResources()));
        known.add(new LanguageChineseSimplifiedDTO(applicationContext.getResources()));
        known.add(new LanguageChineseTraditionalDTO(applicationContext.getResources()));
        return known;
    }
}
