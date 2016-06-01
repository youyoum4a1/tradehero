package com.ayondo.academy.api.watchlist.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SkipCacheSecurityPerPagedWatchlistKey extends SecurityPerPagedWatchlistKey
{
    public static final String BUNDLE_KEY_SKIP_CACHE = SkipCacheSecurityPerPagedWatchlistKey.class.getName() + ".skipCache";

    @Nullable public final Boolean skipCache;

    //<editor-fold desc="Constructors">
    public SkipCacheSecurityPerPagedWatchlistKey(
            @Nullable Integer page,
            @Nullable Integer perPage,
            @Nullable Integer securityId,
            @Nullable Boolean skipCache)
    {
        super(page, perPage, securityId);
        this.skipCache = skipCache;
    }

    public SkipCacheSecurityPerPagedWatchlistKey(@NonNull Bundle args)
    {
        super(args);
        this.skipCache = args.containsKey(BUNDLE_KEY_SKIP_CACHE) ? args.getBoolean(BUNDLE_KEY_SKIP_CACHE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (skipCache == null ? 0 : skipCache.hashCode());
    }

    @Override protected boolean equalFields(@NonNull SecurityPerPagedWatchlistKey other)
    {
        return other instanceof SkipCacheSecurityPerPagedWatchlistKey
                && equalFields((SkipCacheSecurityPerPagedWatchlistKey) other);
    }

    protected boolean equalFields(@NonNull SkipCacheSecurityPerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.skipCache == null ? other.skipCache == null : this.skipCache.equals(other.skipCache));
    }
}
