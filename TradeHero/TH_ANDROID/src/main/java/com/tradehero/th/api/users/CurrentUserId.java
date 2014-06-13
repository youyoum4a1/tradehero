package com.tradehero.th.api.users;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.IntPreference;
import org.jetbrains.annotations.NotNull;

public class CurrentUserId extends IntPreference
{
    public CurrentUserId(SharedPreferences preference, String key, Integer defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @NotNull public UserBaseKey toUserBaseKey()
    {
        return new UserBaseKey(get());
    }
}
