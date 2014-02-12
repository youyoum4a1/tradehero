package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class FriendsSortedPerPagedLeaderboardKey extends SortedPerPagedLeaderboardKey
{
    public final static String BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND = FriendsSortedPerPagedLeaderboardKey.class.getName() + ".includeFoF";

    public final Boolean includeFoF;

    //<editor-fold desc="Constructors">
    public FriendsSortedPerPagedLeaderboardKey(Integer leaderboardDefKey, int page, int perPage, int sortType, boolean includeFoF)
    {
        super(leaderboardDefKey, page, perPage, sortType);
        this.includeFoF = includeFoF;
    }

    public FriendsSortedPerPagedLeaderboardKey(Bundle args)
    {
        super(args);
        this.includeFoF = args.containsKey(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) ? args.getBoolean(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (includeFoF == null ? 0 : includeFoF.hashCode());
    }

    @Override public boolean equals(SortedPerPagedLeaderboardKey other)
    {
        return super.equals(other) && other instanceof FriendsSortedPerPagedLeaderboardKey &&
                equals((FriendsSortedPerPagedLeaderboardKey) other);
    }

    public boolean equals(FriendsSortedPerPagedLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (includeFoF == null ? other.includeFoF == null : includeFoF.equals(other.includeFoF));
    }

    public int compareTo(FriendsSortedPerPagedLeaderboardKey other)
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

        return includeFoF.compareTo(other.includeFoF);
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (includeFoF == null)
        {
            args.remove(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND);
        }
        else
        {
            args.putBoolean(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND, includeFoF);
        }
    }
}
