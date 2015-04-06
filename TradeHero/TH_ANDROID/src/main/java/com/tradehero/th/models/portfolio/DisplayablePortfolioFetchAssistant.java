package com.tradehero.th.models.portfolio;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
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
