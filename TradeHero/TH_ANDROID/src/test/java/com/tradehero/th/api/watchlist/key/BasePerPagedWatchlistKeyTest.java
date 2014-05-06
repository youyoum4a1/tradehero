package com.tradehero.th.api.watchlist.key;


abstract public class BasePerPagedWatchlistKeyTest extends BasePagedWatchlistKeyTest
{
    public static final String TAG = BasePerPagedWatchlistKeyTest.class.getSimpleName();

    protected PerPagedWatchlistKey getPagedNullPerPagedNull()
    {
        return new PerPagedWatchlistKey(null, null);
    }

    protected PerPagedWatchlistKey getPaged1PerPagedNull()
    {
        return new PerPagedWatchlistKey(1, null);
    }

    protected PerPagedWatchlistKey getPaged2PerPagedNull()
    {
        return new PerPagedWatchlistKey(2, null);
    }

    protected PerPagedWatchlistKey getPagedNullPerPaged3()
    {
        return new PerPagedWatchlistKey(null, 3);
    }

    protected PerPagedWatchlistKey getPaged1PerPaged3()
    {
        return new PerPagedWatchlistKey(1, 3);
    }

    protected PerPagedWatchlistKey getPaged2PerPaged3()
    {
        return new PerPagedWatchlistKey(2, 3);
    }

    protected PerPagedWatchlistKey getPagedNullPerPaged4()
    {
        return new PerPagedWatchlistKey(null, 4);
    }

    protected PerPagedWatchlistKey getPaged1PerPaged4()
    {
        return new PerPagedWatchlistKey(1, 4);
    }

    protected PerPagedWatchlistKey getPaged2PerPaged4()
    {
        return new PerPagedWatchlistKey(2, 4);
    }
}
