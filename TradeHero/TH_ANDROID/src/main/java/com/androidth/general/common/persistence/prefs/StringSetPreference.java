package com.androidth.general.common.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StringSetPreference extends AbstractPreference<Set<String>>
{
    public StringSetPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public Set<String> get()
    {
        return preference.getStringSet(key, defaultValue);
    }

    @Override public void set(@Nullable Set<String> value)
    {
        preference.edit().putStringSet(key, value).apply();
    }

    /**
     * Adds all and saves
     */
    public void add(Collection<String> values)
    {
        if (values == null)
        {
            return;
        }

        Set<String> current = get();
        if (current == null)
        {
            current = new HashSet<>();
        }

        for (String value: values)
        {
            current.add(value);
        }

        preference.edit().putStringSet(key, current).apply();
    }
}
