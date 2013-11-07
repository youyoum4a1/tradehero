package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PagedLeaderboardMarkUserId extends LeaderboardMarkUserId
{
    public final static String BUNDLE_KEY_PAGE = PagedLeaderboardMarkUserId.class.getName() + ".page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedLeaderboardMarkUserId(Integer lbmuId, int page)
    {
        super(lbmuId);
        this.page = page;
    }

    public PagedLeaderboardMarkUserId(Bundle args)
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
        return (other instanceof PagedLeaderboardMarkUserId) && equals((PagedLeaderboardMarkUserId) other);
    }

    public boolean equals(PagedLeaderboardMarkUserId other)
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

        if (o.getClass() == PagedLeaderboardMarkUserId.class)
        {
            return compareTo((PagedLeaderboardMarkUserId) o);
        }
        return o.getClass().getName().compareTo(PagedLeaderboardMarkUserId.class.getName());
    }

    public int compareTo(PagedLeaderboardMarkUserId other)
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
        return String.format("[key=%d; page=%d]", key, page);
    }
}
