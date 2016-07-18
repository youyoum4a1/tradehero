package com.androidth.general.network.service;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTOList;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.SuggestHeroesListType;
import com.androidth.general.api.users.SuggestHeroesListTypeNew;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.prefs.IsOnBoardShown;
import com.androidth.general.persistence.social.HeroListCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class UserServiceWrapperStub extends UserServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public UserServiceWrapperStub(
            @NonNull Context context,
            @NonNull UserServiceRx userServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilRx dtoCacheUtil,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<HeroListCacheRx> heroListCache,
            @NonNull Lazy<ProviderListCacheRx> providerListCache,
            @NonNull Provider<UserFormDTO.Builder2> userFormBuilderProvider,
            @NonNull @IsOnBoardShown BooleanPreference isOnBoardShown)
    {
        super(context,
                userServiceRx,
                currentUserId,
                dtoCacheUtil,
                userProfileCache,
                portfolioCompactListCache,
                userMessagingRelationshipCache,
                heroListCache,
                providerListCache,
                userFormBuilderProvider,
                isOnBoardShown);
    }
    //</editor-fold>

    @NonNull @Override protected Observable<LeaderboardUserDTOList> suggestHeroesRx(final @NonNull SuggestHeroesListTypeNew key)
    {
        return super.suggestHeroesRx(key)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends LeaderboardUserDTOList>>()
                {
                    @Override public Observable<? extends LeaderboardUserDTOList> call(Throwable throwable)
                    {
                        return suggestHeroesRx(new SuggestHeroesListType(
                                key.exchangeIds == null || key.exchangeIds.isEmpty() ? null : key.exchangeIds.get(0),
                                key.sectorIds == null || key.sectorIds.isEmpty() ? null : key.sectorIds.get(0),
                                key.getPage(),
                                key.perPage));
                    }
                });
    }
}
