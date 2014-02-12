package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PerPagedLeaderboardDefKey extends PagedLeaderboardDefKey
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedLeaderboardDefKey.class.getName() + ".perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedLeaderboardDefKey(Integer leaderboardDefKey, int page, int perPage)
    {
        super(leaderboardDefKey, page);
        this.perPage = perPage;
    }

    public PerPagedLeaderboardDefKey(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(PagedLeaderboardDefKey other)
    {
        return super.equals(other) && other instanceof PerPagedLeaderboardDefKey &&
                equals((PerPagedLeaderboardDefKey) other);
    }

    public boolean equals(PerPagedLeaderboardDefKey other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    public int compareTo(PerPagedLeaderboardDefKey other)
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

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (perPage == null)
        {
            args.remove(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    @Override public String toString()
    {
        return String.format("[key=%d; page=%d; perPage=%d]", key, page, perPage);
    }
}
