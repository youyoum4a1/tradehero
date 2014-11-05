package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LongPreference extends AbstractPreference<Long>
{
    public LongPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            long defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override @NonNull public Long get()
    {
        return preference.getLong(key, defaultValue);
    }

    @Override public void set(@NonNull Long value)
    {
        preference.edit().putLong(key, value).apply();
    }
}
