package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;

public abstract class AbstractPreference<T> implements TypePreference<T>
{
    protected final T defaultValue;
    protected final String key;
    protected final SharedPreferences preference;

    public AbstractPreference(SharedPreferences preference, String key, T defaultValue)
    {
        this.preference = preference;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override public void delete()
    {
        preference.edit().remove(key).apply();
    }

    @Override public boolean isSet()
    {
        return preference.contains(key);
    }
}
