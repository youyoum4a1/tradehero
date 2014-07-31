package com.tradehero.th.api.achievement;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class UserAchievementDTOKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = UserAchievementDTOKey.class.getName() + ".key";

    public UserAchievementDTOKey(Integer key)
    {
        super(key);
    }

    public UserAchievementDTOKey(@NotNull Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
