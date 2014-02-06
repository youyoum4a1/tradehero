package com.tradehero.th.api.users;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.IntPreference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 10:47 AM To change this template use File | Settings | File Templates. */
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
