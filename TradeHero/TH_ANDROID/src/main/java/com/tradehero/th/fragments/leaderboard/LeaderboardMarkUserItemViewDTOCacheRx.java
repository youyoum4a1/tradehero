package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class LeaderboardMarkUserItemViewDTOCacheRx implements DTOCacheRx<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>
{
    @NonNull private final Resources resources;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final LeaderboardCacheRx leaderboardCache;
    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    LeaderboardMarkUserItemViewDTOCacheRx(
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardCacheRx leaderboardCache,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.resources = resources;
        this.currentUserId = currentUserId;
        this.leaderboardCache = leaderboardCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @NonNull @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>> get(@NonNull final PagedLeaderboardKey key)
    {
        return leaderboardCache.get(key)
                .map(new Func1<Pair<LeaderboardKey, LeaderboardDTO>, Pair<PagedLeaderboardKey, LeaderboardDTO>>()
                {
                    @Override public Pair<PagedLeaderboardKey, LeaderboardDTO> call(Pair<LeaderboardKey, LeaderboardDTO> pair)
                    {
                        return Pair.create(key, pair.second);
                    }
                })
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<
                        Pair<PagedLeaderboardKey, LeaderboardDTO>,
                        Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>>>()
                {
                    @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>> call(
                            final Pair<PagedLeaderboardKey, LeaderboardDTO> pair)
                    {
                        return userProfileCache.getOne(currentUserId.toUserBaseKey())
                                .observeOn(Schedulers.computation())
                                .map(new Func1<Pair<UserBaseKey, UserProfileDTO>,
                                        Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>>()
                                {
                                    @Override public Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList> call(
                                            Pair<UserBaseKey, UserProfileDTO> userBaseKeyUserProfileDTOPair)
                                    {
                                        return Pair.create(pair.first,
                                                new LeaderboardMarkUserItemView.DTOList(resources, currentUserId, pair.second,
                                                        userBaseKeyUserProfileDTOPair.second));
                                    }
                                });
                    }
                });
    }

    @Override public void onNext(PagedLeaderboardKey key, LeaderboardMarkUserItemView.DTOList value)
    {
    }

    @Override public void invalidate(@NonNull PagedLeaderboardKey key)
    {
    }

    @Override public void invalidateAll()
    {
    }
}
