package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import java.util.Set;
import javax.inject.Inject;

public class StringSetPreference extends AbstractPreference<Set<String>>
{
    public StringSetPreference(SharedPreferences preference, String key, Set<String> defaultValue)
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
}
