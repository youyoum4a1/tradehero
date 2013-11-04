package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PagedOwnedLbPositionId extends OwnedLbPositionId
{
    public final static String BUNDLE_KEY_PAGE = PagedOwnedLbPositionId.class.getName() + ".page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedOwnedLbPositionId(Integer lbmuId, Integer lbmupId, int page)
    {
        super(lbmuId, lbmupId);
        this.page = page;
    }

    public PagedOwnedLbPositionId(LeaderboardMarkUserId lbmuId, Integer lbmupId, int page)
    {
        super(lbmuId, lbmupId);
        this.page = page;
    }

    public PagedOwnedLbPositionId(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PagedOwnedLbPositionId) && equals((PagedOwnedLbPositionId) other);
    }

    public boolean equals(PagedOwnedLbPositionId other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == PagedOwnedLbPositionId.class)
        {
            return compareTo((PagedOwnedLbPositionId) o);
        }
        return o.getClass().getName().compareTo(PagedOwnedLbPositionId.class.getName());
    }

    public int compareTo(PagedOwnedLbPositionId other)
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

        return page.compareTo(other.page);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && page != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_PAGE, page);
    }

    @Override public String toString()
    {
        return String.format("[leaderboardMarkUserId=%d; leaderboardMarkUserPositionId=%d; page=%d]", leaderboardMarkUserId, leaderboardMarkUserPositionId, page);
    }
}
