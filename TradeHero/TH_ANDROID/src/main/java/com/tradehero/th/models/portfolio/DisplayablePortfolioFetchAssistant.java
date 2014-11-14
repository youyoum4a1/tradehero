package com.tradehero.th.models.portfolio;

import android.util.Pair;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import rx.Observable;

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
                .flatMap(this::getDisplayablePortfolios);
    }

    @NonNull protected Observable<DisplayablePortfolioDTOList> getDisplayablePortfolios(@NonNull Pair<UserBaseKey, PortfolioCompactDTOList> pair)
    {
        return getDisplayablePortfolios(pair.first, pair.second);
    }

    @NonNull protected Observable<DisplayablePortfolioDTOList> getDisplayablePortfolios(@NonNull UserBaseKey userBaseKey,
            @NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey).map(pair -> pair.second).take(1),
                getPortfolios(portfolioCompactDTOs),
                DisplayablePortfolioDTOList::new);
    }

    @NonNull protected Observable<PortfolioDTOList> getPortfolios(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.from(portfolioCompactDTOs)
                .flatMap(portfolioCompact -> portfolioCache.get(portfolioCompact.getOwnedPortfolioId()).take(1))
                .map(pair -> pair.second)
                .toList()
                .map(PortfolioDTOList::new)
                .take(1);
    }
}
