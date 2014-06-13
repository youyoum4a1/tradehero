package com.tradehero.th.api.competition.key;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.key.SecurityListType;

public class WarrantProviderSecurityListType extends ProviderSecurityListType
{
    //<editor-fold desc="Constructors">
    public WarrantProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
    }

    public WarrantProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
    }

    public WarrantProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(providerId, page);
    }

    public WarrantProviderSecurityListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override public boolean equals(SecurityListType other)
    {
        return WarrantProviderSecurityListType.class.isInstance(other) && super.equals(ProviderSecurityListType.class.cast(other));
    }
}
