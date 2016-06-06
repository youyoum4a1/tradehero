package com.androidth.general.api.level.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class LevelDefId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = LevelDefId.class.getName()+".key";

    public LevelDefId(Integer key)
    {
        super(key);
    }

    public LevelDefId(@NonNull Bundle args)
    {
        super(args);
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
