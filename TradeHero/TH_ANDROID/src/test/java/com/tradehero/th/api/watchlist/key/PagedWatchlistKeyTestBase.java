package com.ayondo.academy.api.watchlist.key;

abstract public class PagedWatchlistKeyTestBase
{
    protected PagedWatchlistKey getPagedNull()
    {
        return new PagedWatchlistKey((Integer) null);
    }

    protected PagedWatchlistKey getPaged1()
    {
        return new PagedWatchlistKey(1);
    }

    protected PagedWatchlistKey getPaged2()
    {
        return new PagedWatchlistKey(2);
    }
}
