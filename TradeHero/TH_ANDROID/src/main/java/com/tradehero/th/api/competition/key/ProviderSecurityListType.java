package com.tradehero.th.api.competition.key;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.key.SecurityListType;


abstract public class ProviderSecurityListType extends SecurityListType
{
    public static final String TAG = ProviderSecurityListType.class.getSimpleName();
    
    private final ProviderId providerId;

    //<editor-fold desc="Constructors">
    protected ProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
        this.providerId = other.providerId;
        validate();
    }

    protected ProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(page, perPage);
        this.providerId = providerId;
        validate();
    }

    protected ProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(page);
        this.providerId = providerId;
        validate();
    }

    protected ProviderSecurityListType(ProviderId providerId)
    {
        super();
        this.providerId = providerId;
        validate();
    }
    //</editor-fold>

    private void validate()
    {
        if (providerId == null)
        {
            throw new NullPointerException("Null is not a valid ProviderId");
        }
    }

    public ProviderId getProviderId()
    {
        return providerId;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ providerId.hashCode();
    }

    @Override public boolean equals(SecurityListType other)
    {
        return ProviderSecurityListType.class.isInstance(other) && equals(ProviderSecurityListType.class.cast(other));
    }

    public boolean equals(ProviderSecurityListType other)
    {
        return super.equals(other) && providerId.equals(other.providerId);
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(SecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!ProviderSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return ProviderSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(ProviderSecurityListType.class.cast(another));
    }

    public int compareTo(ProviderSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        int providerIdCompare = providerId.compareTo(another.providerId);
        if (providerIdCompare != 0)
        {
            return providerIdCompare;
        }
        return super.compareTo(another);
    }
    //</editor-fold>

}
