package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func2;

class ProcessableLeaderboardFriendsCache implements DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>
{
    @NonNull private final LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final FriendLeaderboardUserDTOFactory factory;

    //<editor-fold desc="Constructors">
    public ProcessableLeaderboardFriendsCache(
            @NonNull LeaderboardFriendsCacheRx leaderboardFriendsCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull FriendLeaderboardUserDTOFactory factory)
    {
        this.leaderboardFriendsCache = leaderboardFriendsCache;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
        this.factory = factory;
    }
    //</editor-fold>

    @NonNull @Override public Observable<Pair<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>> get(@NonNull LeaderboardFriendsKey key)
    {
        return Observable.combineLatest(
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                leaderboardFriendsCache.get(key),
                new Func2<UserProfileDTO,
                        Pair<LeaderboardFriendsKey, LeaderboardFriendsDTO>,
                        Pair<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>>()
                {
                    @Override public Pair<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO> call(
                            UserProfileDTO userProfile,
                            Pair<LeaderboardFriendsKey, LeaderboardFriendsDTO> pair)
                    {
                        return Pair.create(
                                pair.first,
                                new ProcessableLeaderboardFriendsDTO(factory, pair.second, userProfile));
                    }
                });
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
