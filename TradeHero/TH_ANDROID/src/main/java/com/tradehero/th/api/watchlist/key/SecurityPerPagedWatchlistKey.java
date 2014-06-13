package com.tradehero.th.api.watchlist.key;

import android.os.Bundle;

public class SecurityPerPagedWatchlistKey extends PerPagedWatchlistKey
{
    public static final String BUNDLE_KEY_SECURITY_ID = SecurityPerPagedWatchlistKey.class.getName() + ".securityId";

    public final Integer securityId;

    //<editor-fold desc="Constructors">
    public SecurityPerPagedWatchlistKey(Integer page, Integer perPage, Integer securityId)
    {
        super(page, perPage);
        this.securityId = securityId;
    }

    public SecurityPerPagedWatchlistKey(Bundle args)
    {
        super(args);
        this.securityId = args.containsKey(BUNDLE_KEY_SECURITY_ID) ? args.getInt(BUNDLE_KEY_SECURITY_ID) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (securityId == null ? 0 : securityId.hashCode());
    }

    protected boolean equals(SecurityPerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.securityId == null ? other.securityId == null : this.securityId.equals(other.securityId));
    }
}
