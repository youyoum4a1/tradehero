package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.api.leaderboard.position.LeaderboardFriendsDTO;
import com.androidth.general.api.leaderboard.position.LeaderboardFriendsKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func2;

class ProcessableLeaderboardFriendsCache implements DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO>
{
    @NonNull private final Resources resources;
    @NonNull private final LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public ProcessableLeaderboardFriendsCache(
            @NonNull Resources resources,
            @NonNull LeaderboardFriendsCacheRx leaderboardFriendsCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull CurrentUserId currentUserId)
    {
        this.resources = resources;
        this.leaderboardFriendsCache = leaderboardFriendsCache;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
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
                                new ProcessableLeaderboardFriendsDTO(new LeaderboardItemDisplayDTO.Factory(resources, currentUserId, userProfile), pair.second, userProfile));
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
