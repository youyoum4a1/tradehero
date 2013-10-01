package com.tradehero.th.persistence.security;

import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 1:46 PM To change this template use File | Settings | File Templates. */
public class SecuritySearchQuery extends SecurityQuery
{
    private static final String searchStringKey = SecuritySearchQuery.class.getName() + ".searchString";
    private static final String pageKey = SecuritySearchQuery.class.getName() + ".page";
    private static final String perPageKey = SecuritySearchQuery.class.getName() + ".perPage";

    public SecuritySearchQuery()
    {
    }

    public SecuritySearchQuery(String searchString)
    {
        setSearchString(searchString);
    }

    public SecuritySearchQuery(String searchString, int page, int perPage)
    {
        setSearchString(searchString);
        setPage(page);
        setPerPage(perPage);
    }

    public String getSearchString()
    {
        return (String)getProperty(searchStringKey);
    }

    public void setSearchString(String searchString)
    {
        setProperty(searchStringKey, searchString);
    }

    public Integer getPage()
    {
        return (Integer)getProperty(pageKey);
    }

    public void setPage(int page)
    {
        setProperty(pageKey, page);
    }

    public Integer getPerPage()
    {
        return (Integer)getProperty(perPageKey);
    }

    public void setPerPage(int perPage)
    {
        setProperty(perPageKey, perPage);
    }
}
