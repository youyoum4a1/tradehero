package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.translation.TranslationToken;

public class TranslationTokenKey implements DTOKey
{
    // There is only 1 for now anyway
    public TranslationTokenKey()
    {
        super();
    }

    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object obj)
    {
        return obj != null && (obj instanceof TranslationTokenKey);
    }
}
