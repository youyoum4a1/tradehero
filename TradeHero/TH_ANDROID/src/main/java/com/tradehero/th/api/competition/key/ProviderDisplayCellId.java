package com.ayondo.academy.api.competition.key;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class ProviderDisplayCellId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = ProviderDisplayCellId.class.getName() + ".key";

    public ProviderDisplayCellId(Integer key)
    {
        super(key);
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
