package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class BooleanPreference extends AbstractPreference<Boolean>
{
    public BooleanPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            boolean defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public Boolean get()
    {
        return preference.getBoolean(key, defaultValue);
    }

    @Override public void set(@NonNull Boolean value)
    {
        preference.edit().putBoolean(key, value).apply();
    }
}
