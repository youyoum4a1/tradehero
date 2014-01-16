package com.tradehero.th.api.competition;

import com.tradehero.th.api.security.SecurityListType;

/**
 * Created by xavier on 1/16/14.
 */
public class WarrantProviderSecurityListType extends ProviderSecurityListType
{
    public static final String TAG = WarrantProviderSecurityListType.class.getSimpleName();

    //<editor-fold desc="Constructors">
    protected WarrantProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
    }

    protected WarrantProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
    }

    protected WarrantProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(providerId, page);
    }

    protected WarrantProviderSecurityListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override public boolean equals(SecurityListType other)
    {
        return WarrantProviderSecurityListType.class.isInstance(other) && super.equals(ProviderSecurityListType.class.cast(other));
    }
}
