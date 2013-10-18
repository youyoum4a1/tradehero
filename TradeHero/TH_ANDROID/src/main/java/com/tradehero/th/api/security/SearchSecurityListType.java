package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:12 PM To change this template use File | Settings | File Templates. */
public class SearchSecurityListType extends SecurityListType
{
    public static final String TAG = SearchSecurityListType.class.getSimpleName();

    //<editor-fold desc="Fields">
    private final String searchString;
    private final int page;
    private final int perPage;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public SearchSecurityListType(String searchString, int page, int perPage)
    {
        this.searchString = searchString;
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    @Override public boolean equals(SecurityListType other)
    {
        if (!(other instanceof SearchSecurityListType))
        {
            return false;
        }
        return equals((SearchSecurityListType) other);
    }

    public boolean equals(SearchSecurityListType other)
    {
        if (other == null)
        {
            return false;
        }
        return (searchString == null ? other.searchString == null : searchString.equals(other.searchString)) &&
                page == other.page &&
                perPage == other.perPage;
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType == null)
        {
            return 1;
        }
        if (!(securityListType instanceof SearchSecurityListType))
        {
            return SearchSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
        }

        SearchSecurityListType searchSecurityListType = (SearchSecurityListType) securityListType;

        int stringCompare = searchString.compareTo(searchSecurityListType.searchString);
        if (stringCompare != 0)
        {
            return stringCompare;
        }
        int pageCompare = new Integer(page).compareTo(new Integer(searchSecurityListType.page));
        if (pageCompare != 0)
        {
            return pageCompare;
        }
        return new Integer(perPage).compareTo(new Integer(searchSecurityListType.perPage));
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public int getPage()
    {
        return page;
    }

    public int getPerPage()
    {
        return perPage;
    }

    public String getSearchString()
    {
        return searchString;
    }
    //</editor-fold>

    @Override public String makeKey()
    {
        return String.format("%s:%d:%d", searchString, page, perPage);
    }

    @Override public String toString()
    {
        return String.format("[%s: searchString=%s; page=%d; perPage=%d]", SearchSecurityListType.class.getName(), searchString, page, perPage);
    }
}
