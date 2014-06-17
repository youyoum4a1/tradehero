package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class BooleanPreference extends AbstractPreference<Boolean>
{
    @Inject public BooleanPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            boolean defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NotNull public Boolean get()
    {
        return preference.getBoolean(key, defaultValue);
    }

    @Override public void set(@NotNull Boolean value)
    {
        preference.edit().putBoolean(key, value).apply();
    }
}
