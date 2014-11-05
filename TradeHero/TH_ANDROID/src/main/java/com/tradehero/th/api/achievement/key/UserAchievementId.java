package com.tradehero.th.api.achievement.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.utils.achievement.AchievementModule;
import com.tradehero.th.utils.broadcast.BroadcastData;
import android.support.annotation.NonNull;

public class UserAchievementId extends AbstractIntegerDTOKey implements BroadcastData
{
    private static final String BUNDLE_KEY = UserAchievementId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserAchievementId(@NonNull Integer key)
    {
        super(key);
    }

    public UserAchievementId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return AchievementModule.KEY_USER_ACHIEVEMENT_ID;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return AchievementModule.ACHIEVEMENT_INTENT_ACTION_NAME;
    }
}
