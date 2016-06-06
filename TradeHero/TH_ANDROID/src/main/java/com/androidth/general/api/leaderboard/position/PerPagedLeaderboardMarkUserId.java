package com.androidth.general.api.leaderboard.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PerPagedLeaderboardMarkUserId extends PagedLeaderboardMarkUserId
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedLeaderboardMarkUserId.class.getName() + ".perPage";

    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedLeaderboardMarkUserId(int lbmuId, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(lbmuId, page);
        this.perPage = perPage;
    }

    public PerPagedLeaderboardMarkUserId(@NonNull Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    public static boolean isPerPagedLeaderboardMarkUserId(@NonNull Bundle args)
    {
        return isPagedLeaderboardMarkUserId(args)
                && args.containsKey(BUNDLE_KEY_PER_PAGE);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    public boolean equals(@Nullable PerPagedLeaderboardMarkUserId other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    public int compareTo(@NonNull PerPagedLeaderboardMarkUserId other)
    {
        if (this == other)
        {
            return 0;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        if (perPage == null)
        {
            return other.perPage == null ? 0 : 1;
        }

        return perPage.compareTo(other.perPage);
    }

    @Override public void putParameters(@NonNull Bundle args)
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
