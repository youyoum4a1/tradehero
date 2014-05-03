package com.tradehero.th.api.watchlist.key;


abstract public class BasePagedWatchlistKeyTest
{
    public static final String TAG = BasePagedWatchlistKeyTest.class.getSimpleName();

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
