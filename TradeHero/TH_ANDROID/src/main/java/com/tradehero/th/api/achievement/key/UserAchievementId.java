package com.tradehero.th.api.achievement.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.utils.achievement.AchievementModule;
import com.tradehero.th.utils.broadcast.BroadcastData;
import org.jetbrains.annotations.NotNull;

public class UserAchievementId extends AbstractIntegerDTOKey implements BroadcastData
{
    private static final String BUNDLE_KEY = UserAchievementId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserAchievementId(@NotNull Integer key)
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

    @NotNull @Override public String getBroadcastBundleKey()
    {
        return AchievementModule.KEY_USER_ACHIEVEMENT_ID;
    }

    @NotNull @Override public String getBroadcastIntentActionName()
    {
        return AchievementModule.ACHIEVEMENT_INTENT_ACTION_NAME;
    }
}
