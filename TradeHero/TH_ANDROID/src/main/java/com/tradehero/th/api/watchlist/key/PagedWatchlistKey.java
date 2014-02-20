package com.tradehero.th.api.watchlist.key;

import android.os.Bundle;
import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import timber.log.Timber;

/**
 * Created by xavier on 2/14/14.
 */
public class PagedWatchlistKey
{
    public static final String BUNDLE_KEY_PAGE = PagedWatchlistKey.class.getName() + ".page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedWatchlistKey(Integer page)
    {
        this.page = page;
    }

    public PagedWatchlistKey(Bundle args)
    {
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return page == null ? 0 : page.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null &&
            getClass().isInstance(other) &&
            other.getClass().isInstance(this) &&
            equals(getClass().cast(other));
    }

    protected boolean equals(PagedWatchlistKey other)
    {
        return other != null &&
                getClass().isInstance(other) &&
                other.getClass().isInstance(this) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    @Override public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            Timber.e("Failed toString", e);
        }
        return "";
    }
}
