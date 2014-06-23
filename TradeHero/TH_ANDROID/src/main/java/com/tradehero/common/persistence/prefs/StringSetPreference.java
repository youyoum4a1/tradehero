package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringSetPreference extends AbstractPreference<Set<String>>
{
    public StringSetPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NotNull public Set<String> get()
    {
        return preference.getStringSet(key, defaultValue);
    }

    @Override public void set(@Nullable Set<String> value)
    {
        preference.edit().putStringSet(key, value).apply();
    }
}
