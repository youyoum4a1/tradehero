package com.tradehero.common.persistence.prefs;

import android.content.SharedPreferences;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 5:52 PM Copyright (c) TradeHero
 */
public class StringPreference extends AbstractPreference<String>
{
    @Inject public StringPreference(SharedPreferences preference, String key, String defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @Override public String get()
    {
        return preference.getString(key, defaultValue);
    }

    @Override public void set(String value)
    {
        preference.edit().putString(key, value).apply();
    }
}
