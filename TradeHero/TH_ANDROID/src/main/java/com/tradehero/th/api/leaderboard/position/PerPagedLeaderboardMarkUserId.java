package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;


public class PerPagedLeaderboardMarkUserId extends PagedLeaderboardMarkUserId
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedLeaderboardMarkUserId.class.getName() + ".perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedLeaderboardMarkUserId(Integer lbmuId, int page, int perPage)
    {
        super(lbmuId, page);
        this.perPage = perPage;
    }

    public PerPagedLeaderboardMarkUserId(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    public boolean equals(PerPagedLeaderboardMarkUserId other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    public int compareTo(PerPagedLeaderboardMarkUserId other)
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
        return String.format("[key=%d; page=%d; perPage=%d]", key, page, perPage);
    }
}
