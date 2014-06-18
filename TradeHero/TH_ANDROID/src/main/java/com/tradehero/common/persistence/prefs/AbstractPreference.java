package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPreference<T> implements TypePreference<T>
{
    @NotNull protected final SharedPreferences preference;
    @NotNull protected final String key;
    @NotNull protected final T defaultValue;

    public AbstractPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull T defaultValue)
    {
        this.preference = preference;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override @NotNull abstract public T get();

    @Override public void delete()
    {
        preference.edit().remove(key).apply();
    }

    @Override public boolean isSet()
    {
        return preference.contains(key);
    }
}
