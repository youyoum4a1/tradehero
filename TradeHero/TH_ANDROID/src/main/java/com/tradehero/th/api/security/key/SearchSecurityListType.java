package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;

public class SearchSecurityListType extends SecurityListType
{
    public final String searchString;

    //<editor-fold desc="Constructors">
    public SearchSecurityListType(String searchString, Integer page, Integer perPage)
    {
        super(page, perPage);
        this.searchString = searchString;
    }

    public SearchSecurityListType(String searchString, Integer page)
    {
        super(page);
        this.searchString = searchString;
    }

    public SearchSecurityListType(String searchString)
    {
        super();
        this.searchString = searchString;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (searchString == null ? 0 : searchString.hashCode()) ^
                super.hashCode();
    }

    @Override protected boolean equalFields(@NonNull SecurityListType other)
    {
        return other instanceof SearchSecurityListType
                && equalFields((SearchSecurityListType) other);
    }

    protected boolean equalFields(@NonNull SearchSecurityListType other)
    {
        return super.equalFields(other) &&
                (searchString == null ? other.searchString == null : searchString.equals(other.searchString));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(@NonNull SecurityListType another)
    {
        if (!SearchSecurityListType.class.isInstance(another))
        {
            return SearchSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
        }

        return compareTo(SearchSecurityListType.class.cast(another));
    }

    public int compareTo(SearchSecurityListType another)
    {
        int stringCompare = searchString.compareTo(another.searchString);
        if (stringCompare != 0)
        {
            return stringCompare;
        }
        return super.compareTo(another);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SearchSecurityListType{" +
                "searchString='" + searchString + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
