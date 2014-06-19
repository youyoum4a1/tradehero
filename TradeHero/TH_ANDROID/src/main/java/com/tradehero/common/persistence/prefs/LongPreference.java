package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongPreference extends AbstractPreference<Long>
{
    @Inject public LongPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            long defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NotNull public Long get()
    {
        return preference.getLong(key, defaultValue);
    }

    @Override public void set(@NotNull Long value)
    {
        preference.edit().putLong(key, value).apply();
    }
}
