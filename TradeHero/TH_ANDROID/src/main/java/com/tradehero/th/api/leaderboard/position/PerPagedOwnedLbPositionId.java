package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PerPagedOwnedLbPositionId extends PagedOwnedLbPositionId
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedOwnedLbPositionId.class.getName() + ".perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedOwnedLbPositionId(Integer lbmuId, Integer lbmupId, int page, int perPage)
    {
        super(lbmuId, lbmupId, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedLbPositionId(LeaderboardMarkUserId lbmuId, Integer lbmupId, int page, int perPage)
    {
        super(lbmuId, lbmupId, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedLbPositionId(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PerPagedOwnedLbPositionId) && equals((PerPagedOwnedLbPositionId) other);
    }

    public boolean equals(PerPagedOwnedLbPositionId other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == PerPagedOwnedLbPositionId.class)
        {
            return compareTo((PerPagedOwnedLbPositionId) o);
        }
        return o.getClass().getName().compareTo(PerPagedOwnedLbPositionId.class.getName());
    }

    public int compareTo(PerPagedOwnedLbPositionId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return perPage.compareTo(other.perPage);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && perPage != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    @Override public String toString()
    {
        return String.format("[leaderboardMarkUserId=%d; leaderboardMarkUserPositionId=%d; page=%d; perPage=%d]", leaderboardMarkUserId, leaderboardMarkUserPositionId, page, perPage);
    }
}
