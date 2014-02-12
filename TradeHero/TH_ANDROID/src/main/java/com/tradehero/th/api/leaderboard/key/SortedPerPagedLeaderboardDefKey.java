package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class SortedPerPagedLeaderboardDefKey extends PerPagedLeaderboardDefKey
{
    public final static String BUNDLE_KEY_SORT_TYPE = SortedPerPagedLeaderboardDefKey.class.getName() + ".sortType";

    public final Integer sortType;

    //<editor-fold desc="Constructors">
    public SortedPerPagedLeaderboardDefKey(Integer leaderboardDefKey, int page, int perPage, int sortType)
    {
        super(leaderboardDefKey, page, perPage);
        this.sortType = sortType;
    }

    public SortedPerPagedLeaderboardDefKey(Bundle args)
    {
        super(args);
        this.sortType = args.containsKey(BUNDLE_KEY_SORT_TYPE) ? args.getInt(BUNDLE_KEY_SORT_TYPE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (sortType == null ? 0 : sortType.hashCode());
    }

    @Override public boolean equals(PerPagedLeaderboardDefKey other)
    {
        return super.equals(other) && other instanceof SortedPerPagedLeaderboardDefKey &&
                equals((SortedPerPagedLeaderboardDefKey) other);
    }

    public boolean equals(SortedPerPagedLeaderboardDefKey other)
    {
        return other != null &&
                super.equals(other) &&
                (sortType == null ? other.sortType == null : sortType.equals(other.sortType));
    }

    public int compareTo(SortedPerPagedLeaderboardDefKey other)
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

    @Override public String toString()
    {
        return String.format("[key=%d; page=%d; perPage=%d; sortType=%d]", key, page, perPage, sortType);
    }
}
