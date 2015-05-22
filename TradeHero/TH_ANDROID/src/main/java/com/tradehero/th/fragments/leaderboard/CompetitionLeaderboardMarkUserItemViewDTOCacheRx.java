package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

class CompetitionLeaderboardMarkUserItemViewDTOCacheRx implements DTOCacheRx<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>
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

    @NonNull @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>> get(@NonNull PagedLeaderboardKey key)
    {
        return this.competitionLeaderboardCache.get((CompetitionLeaderboardId) key)
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<
                        Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO>,
                        Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>>>()
                {
                    @Override public Observable<Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>> call(
                            final Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO> pair)
                    {
                        return getRequisite()
                                .observeOn(Schedulers.computation())
                                .map(new Func1<Requisite,
                                        Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList>>()
                                {
                                    @Override public Pair<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList> call(
                                            Requisite requisite)
                                    {
                                        return Pair.create((PagedLeaderboardKey) pair.first,
                                                (LeaderboardMarkUserItemView.DTOList) new CompetitionLeaderboardMarkUserItemView.DTOList(
                                                        resources,
                                                        currentUserId,
                                                        pair.second,
                                                        requisite.currentUserProfile,
                                                        requisite.provider));
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