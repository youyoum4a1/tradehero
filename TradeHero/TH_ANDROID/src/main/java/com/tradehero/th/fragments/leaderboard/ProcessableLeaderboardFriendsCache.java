package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import rx.Observable;

class ProcessableLeaderboardFriendsCache implements DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>
{
    @NonNull private final LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @NonNull private final FriendLeaderboardUserDTOFactory factory;

    //<editor-fold desc="Constructors">
    public ProcessableLeaderboardFriendsCache(
            @NonNull LeaderboardFriendsCacheRx leaderboardFriendsCache,
            @NonNull FriendLeaderboardUserDTOFactory factory)
    {
        this.leaderboardFriendsCache = leaderboardFriendsCache;
        this.factory = factory;
    }
    //</editor-fold>

    @NonNull @Override public Observable<Pair<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>> get(@NonNull LeaderboardFriendsKey key)
    {
        return leaderboardFriendsCache.get(key)
                .map(pair -> Pair.create(
                        pair.first,
                        new ProcessableLeaderboardFriendsDTO(factory, pair.second)));
    }

    @Override public void onNext(LeaderboardFriendsKey key, ProcessableLeaderboardFriendsDTO value)
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override public void invalidate(@NonNull LeaderboardFriendsKey key)
    {
        leaderboardFriendsCache.invalidate(key);
    }

    @Override public void invalidateAll()
    {
        leaderboardFriendsCache.invalidateAll();
    }
}
