package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardId;
import com.androidth.general.api.leaderboard.key.PagedLeaderboardKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

class CompetitionLeaderboardMarkUserItemViewDTOCacheRx
        implements DTOCacheRx<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>
{
    @NonNull private final Resources resources;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final ProviderId providerId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final ProviderCacheRx providerCache;
    @NonNull private final CompetitionLeaderboardCacheRx competitionLeaderboardCache;

    //<editor-fold desc="Constructors">
    CompetitionLeaderboardMarkUserItemViewDTOCacheRx(
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull ProviderId providerId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull ProviderCacheRx providerCache,
            @NonNull CompetitionLeaderboardCacheRx competitionLeaderboardCache)
    {
        this.resources = resources;
        this.currentUserId = currentUserId;
        this.providerId = providerId;
        this.userProfileCache = userProfileCache;
        this.providerCache = providerCache;
        this.competitionLeaderboardCache = competitionLeaderboardCache;
    }
    //</editor-fold>

    @NonNull @Override
    public Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>> get(@NonNull PagedLeaderboardKey key)
    {
        return this.competitionLeaderboardCache.get((CompetitionLeaderboardId) key)
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<
                        Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>,
                        Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>>>()
                {
                    @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>> call(
                            final Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO> pair)
                    {
                        return getRequisite()
                                .observeOn(Schedulers.computation())
                                .map(new Func1<Requisite,
                                        Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>>()
                                {
                                    @Override public Pair<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>> call(
                                            Requisite requisite)
                                    {
                                        LeaderboardItemDisplayDTO.Factory factory =
                                                new LeaderboardItemDisplayDTO.Factory(resources, currentUserId, requisite.currentUserProfile);

                                        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO> list =
                                                new LeaderboardItemDisplayDTO.DTOList<>(pair.second.leaderboard);
                                        for (LeaderboardUserDTO leaderboardItem : pair.second.leaderboard.getList())
                                        {
                                            list.add(factory.create(leaderboardItem, requisite.provider, pair.second));
                                        }
                                        return Pair.create(pair.first, list);
                                    }
                                });
                    }
                });
    }

    @Override public void onNext(PagedLeaderboardKey key, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO> value)
    {
    }

    @Override public void invalidate(@NonNull PagedLeaderboardKey key)
    {
        this.competitionLeaderboardCache.invalidate((CompetitionLeaderboardId) key);
    }

    @Override public void invalidateAll()
    {
        this.competitionLeaderboardCache.invalidateAll();
    }

    @NonNull Observable<Requisite> getRequisite()
    {
        return Observable.zip(
                providerCache.getOne(providerId),
                userProfileCache.getOne(currentUserId.toUserBaseKey()),
                new Func2<Pair<ProviderId, ProviderDTO>, Pair<UserBaseKey, UserProfileDTO>, Requisite>()
                {
                    @Override public Requisite call(Pair<ProviderId, ProviderDTO> providerPair,
                            Pair<UserBaseKey, UserProfileDTO> userProfilePair)
                    {
                        return new Requisite(providerPair, userProfilePair);
                    }
                });
    }

    private static class Requisite
    {
        @NonNull final ProviderDTO provider;
        @NonNull final UserProfileDTO currentUserProfile;

        private Requisite(
                @NonNull Pair<ProviderId, ProviderDTO> providerPair,
                @NonNull Pair<UserBaseKey, UserProfileDTO> currentUserProfilePair)
        {
            this.provider = providerPair.second;
            this.currentUserProfile = currentUserProfilePair.second;
        }
    }
}
