package com.tradehero.th.api.competition.key;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.key.SecurityListType;
import android.support.annotation.NonNull;

public class BasicProviderSecurityListType extends ProviderSecurityListType
{
    //<editor-fold desc="Constructors">
    public BasicProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
    }

    public BasicProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
    }

    public BasicProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(providerId, page);
    }

    public BasicProviderSecurityListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override protected boolean equals(@NonNull SecurityListType other)
    {
        return super.equals(other)
            && other instanceof BasicProviderSecurityListType;
    }
}
