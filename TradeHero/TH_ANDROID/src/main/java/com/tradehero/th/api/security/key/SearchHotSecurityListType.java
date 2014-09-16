package com.tradehero.th.api.security.key;

public class SearchHotSecurityListType extends SecurityListType
{

    //<editor-fold desc="Constructors">
    public SearchHotSecurityListType(Integer page, Integer perPage)
    {
        super(page, perPage);
    }

    public SearchHotSecurityListType(Integer page)
    {
        super(page);
    }

    public SearchHotSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public int hashCode()
    {
         return super.hashCode();
    }

    @Override public boolean equals(SecurityListType other)
    {
        return (other instanceof SearchHotSecurityListType) && equals((SearchHotSecurityListType) other);
    }

    public boolean equals(SearchHotSecurityListType other)
    {
        return super.equals(other);
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(SecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        if (!SearchHotSecurityListType.class.isInstance(another))
        {
            return SearchHotSecurityListType.class.getName().compareTo(((Object)another).getClass().getName());
        }

        return compareTo(SearchHotSecurityListType.class.cast(another));
    }

    public int compareTo(SearchHotSecurityListType another)
    {
        return super.compareTo(another);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SearchHotSecurityListType{" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
