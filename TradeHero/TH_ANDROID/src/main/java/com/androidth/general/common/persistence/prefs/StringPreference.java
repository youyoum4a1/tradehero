package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class StringPreference extends AbstractPreference<String>
{
    public StringPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull String defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public String get()
    {
        return preference.getString(key, defaultValue);
    }

    @Override public void set(@NonNull String value)
    {
        preference.edit().putString(key, value).apply();
    }
}
