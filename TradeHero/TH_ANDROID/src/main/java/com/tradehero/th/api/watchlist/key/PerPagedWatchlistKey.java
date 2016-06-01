package com.ayondo.academy.api.watchlist.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PerPagedWatchlistKey extends PagedWatchlistKey
{
    public static final String BUNDLE_KEY_PER_PAGE = PerPagedWatchlistKey.class.getName() + ".perPage";

    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedWatchlistKey(@Nullable Integer page, @Nullable Integer perPage)
    {
        super(page);
        this.perPage = perPage;
    }

    public PerPagedWatchlistKey(@NonNull Bundle args)
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

    @Override protected boolean equalFields(@NonNull PagedWatchlistKey other)
    {
        return other instanceof PerPagedWatchlistKey
                && equalFields((PerPagedWatchlistKey) other);
    }

    protected boolean equalFields(@NonNull PerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.perPage == null ? other.perPage == null : this.perPage.equals(other.perPage));
    }
}
