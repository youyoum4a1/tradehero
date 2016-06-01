package com.ayondo.academy.api.watchlist.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SecurityPerPagedWatchlistKey extends PerPagedWatchlistKey
{
    public static final String BUNDLE_KEY_SECURITY_ID = SecurityPerPagedWatchlistKey.class.getName() + ".securityId";

    @Nullable public final Integer securityId;

    //<editor-fold desc="Constructors">
    public SecurityPerPagedWatchlistKey(
            @Nullable Integer page,
            @Nullable Integer perPage,
            @Nullable Integer securityId)
    {
        super(page, perPage);
        this.securityId = securityId;
    }

    public SecurityPerPagedWatchlistKey(@NonNull Bundle args)
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

    @Override protected boolean equalFields(@NonNull PerPagedWatchlistKey other)
    {
        return other instanceof SecurityPerPagedWatchlistKey
                && equalFields((SecurityPerPagedWatchlistKey) other);
    }

    protected boolean equalFields(@NonNull SecurityPerPagedWatchlistKey other)
    {
        return super.equals(other) &&
                (this.securityId == null ? other.securityId == null : this.securityId.equals(other.securityId));
    }
}
