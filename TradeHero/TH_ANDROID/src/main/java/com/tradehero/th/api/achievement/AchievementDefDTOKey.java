package com.tradehero.th.api.achievement;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class AchievementDefDTOKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = AchievementDefDTOKey.class.getName() +".key";

    public AchievementDefDTOKey(Integer key)
    {
        super(key);
    }

    public AchievementDefDTOKey(@NotNull Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
