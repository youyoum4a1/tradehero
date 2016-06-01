package com.ayondo.academy.api.watchlist.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import timber.log.Timber;

public class PagedWatchlistKey
{
    public static final String BUNDLE_KEY_PAGE = PagedWatchlistKey.class.getName() + ".page";

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedWatchlistKey(@Nullable Integer page)
    {
        this.page = page;
    }

    public PagedWatchlistKey(@NonNull Bundle args)
    {
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return page == null ? 0 : page.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof PagedWatchlistKey
                && equals(getClass().cast(other));
    }

    protected boolean equalFields(@NonNull PagedWatchlistKey other)
    {
        return (page == null ? other.page == null : page.equals(other.page));
    }

    @Override public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        } catch (IOException e)
        {
            Timber.e("Failed toString", e);
        }
        return "";
    }
}
