package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 5:55 PM Copyright (c) TradeHero
 */
public class BooleanPreference extends AbstractPreference<Boolean>
{
    @Inject public BooleanPreference(SharedPreferences preference, String key, Boolean defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public Boolean get()
    {
        return preference.getBoolean(key, defaultValue);
    }

    @Override public void set(Boolean value)
    {
        preference.edit().putBoolean(key, value).apply();
    }
}
