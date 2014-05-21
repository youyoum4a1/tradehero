package com.tradehero.th.api.users;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.IntPreference;

public class CurrentUserId extends IntPreference
{
    public CurrentUserId(SharedPreferences preference, String key, Integer defaultValue)
    {
        super(preference, key, defaultValue);
    }

    public UserBaseKey toUserBaseKey()
    {
        return new UserBaseKey(get());
    }
}
