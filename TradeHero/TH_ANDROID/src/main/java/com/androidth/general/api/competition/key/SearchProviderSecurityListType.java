package com.androidth.general.api.competition.key;

import android.support.annotation.NonNull;
import com.androidth.general.api.competition.ProviderId;

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

    @Override protected boolean equalFields(@NonNull ProviderSecurityListType other)
    {
        return other instanceof SearchProviderSecurityListType
                && equalFields((SearchProviderSecurityListType) other);
    }

    protected boolean equalFields(@NonNull SearchProviderSecurityListType other)
    {
        return super.equalFields(other)
                && searchString.equals(other.searchString);
    }

    @Override public int compareTo(ProviderSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        if (!SearchProviderSecurityListType.class.isInstance(another))
        {
            return SearchProviderSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
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
