package com.tradehero.th.api.users;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.IntPreference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CurrentUserId extends IntPreference
{
    public CurrentUserId(
            @NotNull SharedPreferences preference,
            @NotNull String key,
            int defaultValue)
    {
        super(preference, key, defaultValue);
    }

    @NotNull public UserBaseKey toUserBaseKey()
    {
        return new UserBaseKey(get());
    }
}
