package com.tradehero.th.api.competition.key;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class ProviderDisplayCellId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = ProviderDisplayCellId.class.getName() + ".key";

    public ProviderDisplayCellId(Integer key)
    {
        super(key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
