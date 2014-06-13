package com.tradehero.th.api.watchlist.key;

import android.os.Bundle;

public class PerPagedWatchlistKey extends PagedWatchlistKey
{
    public static final String BUNDLE_KEY_PER_PAGE = PerPagedWatchlistKey.class.getName() + ".perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedWatchlistKey(Integer page, Integer perPage)
    {
        super(page);
        this.perPage = perPage;
    }

    public PerPagedWatchlistKey(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    protected boolean equals(PerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.perPage == null ? other.perPage == null : this.perPage.equals(other.perPage));
    }
}
