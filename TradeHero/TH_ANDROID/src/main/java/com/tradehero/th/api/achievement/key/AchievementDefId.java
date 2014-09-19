package com.tradehero.th.api.achievement.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class AchievementDefId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = AchievementDefId.class.getName() +".key";

    public AchievementDefId(Integer key)
    {
        super(key);
    }

    public AchievementDefId(@NotNull Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
