package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class FriendsPerPagedLeaderboardKey extends PerPagedLeaderboardKey
{
    public final static String BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND = FriendsPerPagedLeaderboardKey.class.getName() + ".includeFoF";

    public final Boolean includeFoF;

    //<editor-fold desc="Constructors">
    public FriendsPerPagedLeaderboardKey(Integer leaderboardDefKey, Integer page, Integer perPage, Boolean includeFoF)
    {
        super(leaderboardDefKey, page, perPage);
        this.includeFoF = includeFoF;
    }

    public FriendsPerPagedLeaderboardKey(FriendsPerPagedLeaderboardKey other, Integer overrideKey, Integer page)
    {
        super(other, overrideKey, page);
        this.includeFoF = other.includeFoF;
    }

    public FriendsPerPagedLeaderboardKey(Bundle args)
    {
        super(args);
        this.includeFoF = args.containsKey(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) ? args.getBoolean(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (includeFoF == null ? 0 : includeFoF.hashCode());
    }

    @Override public boolean equals(PerPagedLeaderboardKey other)
    {
        return super.equals(other) && other instanceof FriendsPerPagedLeaderboardKey &&
                equals((FriendsPerPagedLeaderboardKey) other);
    }

    public boolean equals(FriendsPerPagedLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (includeFoF == null ? other.includeFoF == null : includeFoF.equals(other.includeFoF));
    }

    public int compareTo(FriendsPerPagedLeaderboardKey other)
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

    @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new FriendsPerPagedLeaderboardKey(this, key, page);
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
