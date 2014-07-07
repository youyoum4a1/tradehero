package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class StringPreference extends AbstractPreference<String>
{
    @Inject public StringPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull String defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NotNull public String get()
    {
        return preference.getString(key, defaultValue);
    }

    @Override public void set(@NotNull String value)
    {
        preference.edit().putString(key, value).apply();
    }
}
