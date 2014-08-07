package com.tradehero.th.api.level.key;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class LevelDefId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = LevelDefId.class.getName()+".key";

    public LevelDefId(Integer key)
    {
        super(key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
