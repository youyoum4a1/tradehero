package com.tradehero.th.api.competition.key;

import com.tradehero.th.api.competition.ProviderId;

public class SearchProviderSecurityListType extends ProviderSecurityListType
{
    public final String searchString;

    //<editor-fold desc="Constructors">
    public SearchProviderSecurityListType(ProviderId providerId, String searchString, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
        this.searchString = searchString;
    }

    public SearchProviderSecurityListType(ProviderId providerId, String searchString, Integer page)
    {
        super(providerId, page);
        this.searchString = searchString;
    }

    public SearchProviderSecurityListType(ProviderId providerId, String searchString)
    {
        super(providerId);
        this.searchString = searchString;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (searchString == null ? 0 : searchString.hashCode());
    }

    @Override public boolean equals(ProviderSecurityListType other)
    {
        return (other instanceof SearchProviderSecurityListType) && equals(
                (SearchProviderSecurityListType) other);
    }

    public boolean equals(SearchProviderSecurityListType other)
    {
        return super.equals(other) && searchString.equals(other.searchString);
    }

    @Override public int compareTo(ProviderSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        if (!SearchProviderSecurityListType.class.isInstance(another))
        {
            return SearchProviderSecurityListType.class.getName().compareTo(((Object)another).getClass().getName());
        }

        return compareTo(ProviderSecurityListType.class.cast(another));
    }

    public int compareTo(SearchProviderSecurityListType other)
    {
        int searchCompare = searchString.compareTo(other.searchString);
        if (searchCompare != 0)
        {
            return searchCompare;
        }
        return super.compareTo(other);
    }
}
