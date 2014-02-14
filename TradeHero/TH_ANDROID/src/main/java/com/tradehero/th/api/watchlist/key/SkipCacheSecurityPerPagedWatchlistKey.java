package com.tradehero.th.api.watchlist.key;

import android.os.Bundle;

/**
 * Created by xavier on 2/14/14.
 */
public class SkipCacheSecurityPerPagedWatchlistKey extends SecurityPerPagedWatchlistKey
{
    public static final String TAG = SkipCacheSecurityPerPagedWatchlistKey.class.getSimpleName();
    public static final String BUNDLE_KEY_SKIP_CACHE = SkipCacheSecurityPerPagedWatchlistKey.class.getName() + ".skipCache";

    public final Boolean skipCache;

    //<editor-fold desc="Constructors">
    public SkipCacheSecurityPerPagedWatchlistKey(Integer page, Integer perPage, Integer securityId, Boolean skipCache)
    {
        super(page, perPage, securityId);
        this.skipCache = skipCache;
    }

    public SkipCacheSecurityPerPagedWatchlistKey(Bundle args)
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

    protected boolean equals(SkipCacheSecurityPerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.skipCache == null ? other.skipCache == null : this.skipCache.equals(other.skipCache));
    }
}
