package com.tradehero.th.api.competition;

import com.tradehero.th.api.security.SecurityListType;

/**
 * Created by xavier on 1/16/14.
 */
public class BasicProviderSecurityListType extends ProviderSecurityListType
{
    public static final String TAG = BasicProviderSecurityListType.class.getSimpleName();

    //<editor-fold desc="Constructors">
    protected BasicProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
    }

    protected BasicProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
    }

    protected BasicProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(providerId, page);
    }

    protected BasicProviderSecurityListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override public boolean equals(SecurityListType other)
    {
        return BasicProviderSecurityListType.class.isInstance(other) && super.equals(ProviderSecurityListType.class.cast(other));
    }
}
