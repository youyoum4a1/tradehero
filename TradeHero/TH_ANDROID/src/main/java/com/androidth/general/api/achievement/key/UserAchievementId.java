package com.androidth.general.api.achievement.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;
import com.androidth.general.utils.broadcast.BroadcastData;

import static com.androidth.general.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_ACTION_NAME;
import static com.androidth.general.utils.broadcast.BroadcastConstants.KEY_USER_ACHIEVEMENT_ID;

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

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return KEY_USER_ACHIEVEMENT_ID;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return ACHIEVEMENT_INTENT_ACTION_NAME;
    }
}
