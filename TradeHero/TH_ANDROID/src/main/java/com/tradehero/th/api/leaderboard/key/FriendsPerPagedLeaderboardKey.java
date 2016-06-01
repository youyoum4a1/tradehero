package com.ayondo.academy.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public FriendsPerPagedLeaderboardKey(Bundle args, @Nullable FriendsPerPagedLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.includeFoF = args.containsKey(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) ? args.getBoolean(BUNDLE_KEY_INCLUDE_FRIEND_OF_FRIEND) : ((defaultValues != null) ? defaultValues.includeFoF : null);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (includeFoF == null ? 0 : includeFoF.hashCode());
    }

    @Override public boolean equalFields(@NonNull PerPagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof FriendsPerPagedLeaderboardKey
                && equalFields((FriendsPerPagedLeaderboardKey) other);
    }

    public boolean equalFields(@NonNull FriendsPerPagedLeaderboardKey other)
    {
        return super.equalFields(other) &&
                (includeFoF == null ? other.includeFoF == null : includeFoF.equals(other.includeFoF));
    }

    @NonNull @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new FriendsPerPagedLeaderboardKey(this, id, page);
    }

    @Override public void putParameters(@NonNull Bundle args)
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
