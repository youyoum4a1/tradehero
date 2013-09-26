package com.tradehero.th.persistence;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 6:31 PM Copyright (c) TradeHero */
public class PaginationFilter
{
    private Comparable maxId;
    private Comparable minId;
    private int perPage;

    public PaginationFilter(Comparable maxId, Comparable minId, int perPage)
    {
        this.maxId = maxId;
        this.minId = minId;
        this.perPage = perPage;
    }

    public Comparable getMaxId()
    {
        return maxId;
    }

    public void setMaxId(Comparable maxId)
    {
        this.maxId = maxId;
    }

    public Comparable getMinId()
    {
        return minId;
    }

    public void setMinId(Comparable minId)
    {
        this.minId = minId;
    }

    public int getPerPage()
    {
        return perPage;
    }

    public void setPerPage(int perPage)
    {
        this.perPage = perPage;
    }
}
