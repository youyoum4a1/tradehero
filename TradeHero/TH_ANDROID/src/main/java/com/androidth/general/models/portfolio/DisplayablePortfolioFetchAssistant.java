package com.androidth.general.models.portfolio;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.api.portfolio.DisplayablePortfolioDTOList;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioDTOList;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class DisplayablePortfolioFetchAssistant
{
    @NonNull private final PortfolioCompactListCacheRx portfolioListCache;
    @NonNull private final PortfolioCacheRx portfolioCache;
    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public DisplayablePortfolioFetchAssistant(
            @NonNull PortfolioCompactListCacheRx portfolioListCache,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.portfolioListCache = portfolioListCache;
        this.portfolioCache = portfolioCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @NonNull public Observable<DisplayablePortfolioDTOList> get(@NonNull UserBaseKey userBaseKey)
    {
        return portfolioListCache.get(userBaseKey)
                .flatMap(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, Observable<? extends DisplayablePortfolioDTOList>>()
                {
                    @Override public Observable<? extends DisplayablePortfolioDTOList> call(
                            Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                    {
                        return getDisplayablePortfolios(pair.first, pair.second);
                    }
                });
    }

    @NonNull protected Observable<DisplayablePortfolioDTOList> getDisplayablePortfolios(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()).take(1),
                portfolioCache.getPortfolios(portfolioCompactDTOs, null).take(1),
                new Func2<UserProfileDTO, PortfolioDTOList, DisplayablePortfolioDTOList>()
                {
                    @Override public DisplayablePortfolioDTOList call(UserProfileDTO t1, PortfolioDTOList t2)
                    {
                        return new DisplayablePortfolioDTOList(t1, t2);
                    }
                });
    }
}
