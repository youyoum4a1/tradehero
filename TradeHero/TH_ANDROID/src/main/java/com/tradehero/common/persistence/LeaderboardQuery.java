package com.tradehero.common.persistence;

/** Created with IntelliJ IDEA. User: tho Date: 11/1/13 Time: 10:52 AM Copyright (c) TradeHero */
public class LeaderboardQuery extends Query
{
    private static final String SORT_TYPE = LeaderboardQuery.class.getName() + ".sortType";
    private static String PAGE_KEY = LeaderboardQuery.class.getName() + ".page";

    public Integer getPage()
    {
        return (Integer) getProperty(PAGE_KEY);
    }

    public void setPage(Integer page)
    {
        setProperty(PAGE_KEY, page);
    }

    public Integer getSortType()
    {
        return (Integer) getProperty(SORT_TYPE);
    }

    public void setSortType(Integer sortType)
    {
        setProperty(SORT_TYPE, sortType);
    }

}
