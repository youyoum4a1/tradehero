package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;


public class BooleanPreference extends AbstractPreference<Boolean>
{
    @Inject public BooleanPreference(SharedPreferences preference, String key, Boolean defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public Boolean get()
    {
        return preference.getBoolean(key, defaultValue);
    }

    @Override public void set(Boolean value)
    {
        preference.edit().putBoolean(key, value).apply();
    }
}
