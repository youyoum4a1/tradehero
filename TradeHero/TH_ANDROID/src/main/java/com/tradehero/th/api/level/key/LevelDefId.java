package com.tradehero.th.api.level.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class LevelDefId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = LevelDefId.class.getName()+".key";

    public LevelDefId(Integer key)
    {
        super(key);
    }

    public LevelDefId(@NotNull Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
