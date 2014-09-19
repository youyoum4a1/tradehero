package com.tradehero.th.api.achievement.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class UserAchievementId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = UserAchievementId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserAchievementId(Integer key)
    {
        super(key);
    }

    public UserAchievementId(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
