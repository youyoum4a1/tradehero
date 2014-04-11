package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 5:48 PM Copyright (c) TradeHero
 */
public class LongPreference extends AbstractPreference<Long>
{
    @Inject public LongPreference(SharedPreferences preference, String key, Long defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public Long get()
    {
        return preference.getLong(key, defaultValue);
    }

    @Override public void set(Long value)
    {
        preference.edit().putLong(key, value).apply();
    }
}
