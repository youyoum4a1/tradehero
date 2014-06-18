package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class IntPreference extends AbstractPreference<Integer>
{
    @Inject public IntPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            int defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NotNull public Integer get()
    {
        return preference.getInt(key, defaultValue);
    }

    @Override public void set(@NotNull Integer value)
    {
        preference.edit().putInt(key, value).apply();
    }
}
