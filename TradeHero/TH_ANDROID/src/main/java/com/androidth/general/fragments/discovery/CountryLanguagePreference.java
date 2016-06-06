package com.androidth.general.fragments.discovery;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.prefs.AbstractPreference;
import com.androidth.general.api.news.CountryLanguagePairDTO;

public class CountryLanguagePreference extends AbstractPreference<CountryLanguagePairDTO>
{
    private static final String NAME_KEY_SUFFIX = ".name";
    private static final String COUNTRY_KEY_SUFFIX = ".country";
    private static final String LANGUAGE_KEY_SUFFIX = ".language";
    private final String nameKey;
    private final String countryKey;
    private final String languageKey;

    public CountryLanguagePreference(@NonNull SharedPreferences preference, @NonNull String key, @NonNull CountryLanguagePairDTO defaultValue)
    {
        super(preference, key, defaultValue);

        countryKey = key + COUNTRY_KEY_SUFFIX;
        languageKey = key + LANGUAGE_KEY_SUFFIX;
        nameKey = key + NAME_KEY_SUFFIX;
    }

    @NonNull @Override public CountryLanguagePairDTO get()
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

    @Override public boolean isSet()
    {
        return preference.contains(nameKey) && preference.contains(countryKey) && preference.contains(languageKey);
    }

    @Override public void delete()
    {
        preference.edit()
                .remove(nameKey)
                .remove(countryKey)
                .remove(languageKey)
                .apply();
    }
}
