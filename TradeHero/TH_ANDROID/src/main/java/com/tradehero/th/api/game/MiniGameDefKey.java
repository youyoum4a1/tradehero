package com.tradehero.th.api.game;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class MiniGameDefKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = MiniGameDefKey.class.getName() +".key";

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
