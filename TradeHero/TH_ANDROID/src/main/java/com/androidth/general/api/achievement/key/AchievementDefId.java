package com.androidth.general.api.achievement.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class AchievementDefId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = AchievementDefId.class.getName() +".key";

    //<editor-fold desc="Constructors">
    public AchievementDefId(Integer key)
    {
        super(key);
    }

    public AchievementDefId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
