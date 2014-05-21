package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;

public class IntPreference extends AbstractPreference<Integer>
{
    @Inject public IntPreference(SharedPreferences preference, String key, Integer defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public Integer get()
    {
        return preference.getInt(key, defaultValue);
    }

    @Override public void set(Integer value)
    {
        preference.edit().putInt(key, value).apply();
    }
}
