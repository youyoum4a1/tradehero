package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class LongPreference extends AbstractPreference<Long>
{
    public LongPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            long defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public Long get()
    {
        return preference.getLong(key, defaultValue);
    }

    @Override public void set(@NonNull Long value)
    {
        preference.edit().putLong(key, value).apply();
    }
}
