package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
@Deprecated
public class SortedPerPagedLeaderboardKey extends PerPagedLeaderboardKey
{
    public final static String BUNDLE_KEY_SORT_TYPE = SortedPerPagedLeaderboardKey.class.getName() + ".sortType";

    public final Integer sortType;

    //<editor-fold desc="Constructors">
    public SortedPerPagedLeaderboardKey(Integer leaderboardDefKey, Integer page, Integer perPage, Integer sortType)
    {
        super(leaderboardDefKey, page, perPage);
        this.sortType = sortType;
    }

    public SortedPerPagedLeaderboardKey(SortedPerPagedLeaderboardKey other, Integer overrideKey, Integer page)
    {
        super(other, overrideKey, page);
        this.sortType = other.sortType;
    }

    public SortedPerPagedLeaderboardKey(Bundle args)
    {
        super(args);
        this.sortType = args.containsKey(BUNDLE_KEY_SORT_TYPE) ? args.getInt(BUNDLE_KEY_SORT_TYPE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (sortType == null ? 0 : sortType.hashCode());
    }

    @Override public boolean equals(PerPagedLeaderboardKey other)
    {
        return super.equals(other) && other instanceof SortedPerPagedLeaderboardKey &&
                equals((SortedPerPagedLeaderboardKey) other);
    }

    public boolean equals(SortedPerPagedLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (sortType == null ? other.sortType == null : sortType.equals(other.sortType));
    }

    public int compareTo(SortedPerPagedLeaderboardKey other)
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

        return sortType.compareTo(other.sortType);
    }

    @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new SortedPerPagedLeaderboardKey(this, key, page);
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (sortType == null)
        {
            args.remove(BUNDLE_KEY_SORT_TYPE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_SORT_TYPE, sortType);
        }
    }
}
