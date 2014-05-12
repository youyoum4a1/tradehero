package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;


public class StringSetPreference extends AbstractPreference<Set<String>>
{
    @Inject public StringSetPreference(SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public Set<String> get()
    {
        return preference.getStringSet(key, defaultValue);
    }

    @Override public void set(Set<String> value)
    {
        preference.edit().putStringSet(key, value).apply();
    }

    /**
     * Adds all and saves
     * @param values
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
