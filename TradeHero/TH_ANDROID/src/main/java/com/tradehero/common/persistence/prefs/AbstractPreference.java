package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 5:44 PM Copyright (c) TradeHero
 */
public abstract class AbstractPreference<T>
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

    public abstract T get();
    public abstract void set(T value);

    public void delete()
    {
        preference.edit().remove(key).apply();
    }

    public boolean isSet()
    {
        return preference.contains(key);
    }
}
