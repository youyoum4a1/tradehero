package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public abstract class AbstractPreference<T> implements TypePreference<T>
{
    @NonNull protected final SharedPreferences preference;
    @NonNull protected final String key;
    @NonNull protected final T defaultValue;

    public AbstractPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull T defaultValue)
    {
        this.preference = preference;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override @NonNull abstract public T get();

    @Override public void delete()
    {
        preference.edit().remove(key).apply();
    }

    @Override public boolean isSet()
    {
        return preference.contains(key);
    }
}
