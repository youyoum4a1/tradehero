package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class IntPreference extends AbstractPreference<Integer>
{
    public IntPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            int defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public Integer get()
    {
        return preference.getInt(key, defaultValue);
    }

    @Override public void set(@NonNull Integer value)
    {
        preference.edit().putInt(key, value).apply();
    }
}
