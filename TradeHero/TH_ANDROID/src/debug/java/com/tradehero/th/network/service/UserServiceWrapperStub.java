package com.tradehero.th.network.service;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.api.users.SuggestHeroesListTypeNew;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
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
            @NonNull DTOCacheUtilImpl dtoCacheUtil,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<HeroListCacheRx> heroListCache,
            @NonNull Lazy<ProviderListCacheRx> providerListCache,
            @NonNull Provider<UserFormDTO.Builder2> userFormBuilderProvider)
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
                userFormBuilderProvider);
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
