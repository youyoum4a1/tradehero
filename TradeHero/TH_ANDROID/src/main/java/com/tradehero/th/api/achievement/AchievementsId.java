package com.tradehero.th.api.achievement;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class AchievementsId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = AchievementsId.class.getName() +".key";

    public AchievementsId(Integer key)
    {
        super(key);
    }

    public AchievementsId(@NotNull Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
