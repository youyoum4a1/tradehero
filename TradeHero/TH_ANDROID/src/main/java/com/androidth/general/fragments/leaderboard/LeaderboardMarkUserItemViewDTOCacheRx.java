package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.api.leaderboard.key.PagedLeaderboardKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.leaderboard.LeaderboardCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class LeaderboardMarkUserItemViewDTOCacheRx implements DTOCacheRx<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>
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

    @NonNull @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>> get(
            @NonNull final PagedLeaderboardKey key)
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
                        Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>>>()
                {
                    @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>> call(
                            final Pair<PagedLeaderboardKey, LeaderboardDTO> pair)
                    {
                        return userProfileCache.getOne(currentUserId.toUserBaseKey())
                                .observeOn(Schedulers.computation())
                                .map(new Func1<Pair<UserBaseKey, UserProfileDTO>,
                                        Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>>()
                                {
                                    @Override public Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>> call(
                                            Pair<UserBaseKey, UserProfileDTO> userBaseKeyUserProfileDTOPair)
                                    {

                                        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO> dtos =
                                                new LeaderboardItemDisplayDTO.DTOList<>(pair.second);
                                        LeaderboardItemDisplayDTO.Factory factory =
                                                new LeaderboardItemDisplayDTO.Factory(resources, currentUserId, userBaseKeyUserProfileDTOPair.second);

                                        for (LeaderboardUserDTO leaderboardItem : pair.second.getList())
                                        {
                                            dtos.add(factory.create(leaderboardItem));
                                        }

                                        return Pair.create(pair.first,
                                                dtos);
                                    }
                                });
                    }
                });
    }

    @Override public void onNext(PagedLeaderboardKey key, LeaderboardItemDisplayDTO.DTOList value)
    {
    }

    @Override public void invalidate(@NonNull PagedLeaderboardKey key)
    {
    }

    @Override public void invalidateAll()
    {
    }
}
