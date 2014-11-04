package com.tradehero.th.fragments.discovery;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.AbstractPreference;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class CountryLanguagePreference extends AbstractPreference<CountryLanguagePairDTO>
{
    private static final String NAME_KEY_SUFFIX = ".name";
    private static final String COUNTRY_KEY_SUFFIX = ".country";
    private static final String LANGUAGE_KEY_SUFFIX = ".language";
    private final String nameKey;
    private final String countryKey;
    private final String languageKey;

    @Inject public CountryLanguagePreference(@NotNull SharedPreferences preference, @NotNull String key, @NotNull CountryLanguagePairDTO defaultValue)
    {
        super(preference, key, defaultValue);

        countryKey = key + COUNTRY_KEY_SUFFIX;
        languageKey = key + LANGUAGE_KEY_SUFFIX;
        nameKey = key + NAME_KEY_SUFFIX;
    }

    @NotNull @Override public CountryLanguagePairDTO get()
    {
        String country = preference.getString(countryKey, null);
        String language = preference.getString(languageKey, null);
        String name = preference.getString(nameKey, null);

        if (country == null || language == null || name == null)
        {
            return defaultValue;
        }
        else
        {
            return new CountryLanguagePairDTO(name, country, language);
        }
    }

    @Override public void set(CountryLanguagePairDTO value)
    {
        preference.edit()
                .putString(nameKey, value.name)
                .putString(countryKey, value.countryCode)
                .putString(languageKey, value.languageCode)
                .apply();
    }
}
