package com.tradehero.th.api.security.key;

public class SearchProviderSecurityListType extends SearchSecurityListType
{
    public final int providerId;

    //<editor-fold desc="Constructors">
    public SearchProviderSecurityListType(int providerId, String searchString, Integer page, Integer perPage)
    {
        super(searchString, page, perPage);
        this.providerId = providerId;
    }

    public SearchProviderSecurityListType(int providerId, String searchString, Integer page)
    {
        super(searchString, page);
        this.providerId = providerId;
    }

    public SearchProviderSecurityListType(int providerId, String searchString)
    {
        super(searchString);
        this.providerId = providerId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                Integer.valueOf(providerId).hashCode();
    }

    @Override public boolean equals(SearchSecurityListType other)
    {
        return (other instanceof SearchProviderSecurityListType) && equals(
                (SearchProviderSecurityListType) other);
    }

    public boolean equals(SearchProviderSecurityListType other)
    {
        return super.equals(other) && providerId == other.providerId;
    }

    @Override public int compareTo(SearchSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        if (!SearchProviderSecurityListType.class.isInstance(another))
        {
            return SearchProviderSecurityListType.class.getName().compareTo(((Object)another).getClass().getName());
        }

        return compareTo(SearchSecurityListType.class.cast(another));
    }

    public int compareTo(SearchProviderSecurityListType other)
    {
        int providerCompare = Integer.valueOf(providerId).compareTo(other.providerId);
        if (providerCompare != 0)
        {
            return providerCompare;
        }
        return super.compareTo(other);
    }
}
