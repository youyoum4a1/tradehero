package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;

public class StringPreference extends AbstractPreference<String>
{
    public StringPreference(SharedPreferences preference, String key, String defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public String get()
    {
        return preference.getString(key, defaultValue);
    }

    @Override public void set(String value)
    {
        preference.edit().putString(key, value).apply();
    }
}
