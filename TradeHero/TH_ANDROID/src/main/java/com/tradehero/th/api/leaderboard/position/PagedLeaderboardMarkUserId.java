package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PagedLeaderboardMarkUserId extends LeaderboardMarkUserId
{
    public final static String BUNDLE_KEY_PAGE = PagedLeaderboardMarkUserId.class.getName() + ".page";

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedLeaderboardMarkUserId(int lbmuId, @Nullable Integer page)
    {
        super(lbmuId);
        this.page = page;
    }

    public PagedLeaderboardMarkUserId(@NotNull Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    public static boolean isPagedLeaderboardMarkUserId(@NotNull Bundle args)
    {
        return isLeaderboardMarkUserId(args)
                && args.containsKey(BUNDLE_KEY_PAGE);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0 : page.hashCode());
    }

    public boolean equals(@Nullable PagedLeaderboardMarkUserId other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    public int compareTo(@NotNull PagedLeaderboardMarkUserId other)
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

        if (page == null)
        {
            return other.page == null ? 0 : 1;
        }

        return page.compareTo(other.page);
    }

    @Override public void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        if (page == null)
        {
            args.remove(BUNDLE_KEY_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PAGE, page);
        }
    }

    @Override public String toString()
    {
        return String.format("[key=%d; page=%d]", key, page);
    }
}
