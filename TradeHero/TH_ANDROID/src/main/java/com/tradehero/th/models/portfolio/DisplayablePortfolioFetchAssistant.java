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
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class DisplayablePortfolioFetchAssistant
{
    @NotNull private final PortfolioCompactListCacheRx portfolioListCache;
    @NotNull private final PortfolioCacheRx portfolioCache;
    @NotNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public DisplayablePortfolioFetchAssistant(
            @NotNull PortfolioCompactListCacheRx portfolioListCache,
            @NotNull PortfolioCacheRx portfolioCache,
            @NotNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.portfolioListCache = portfolioListCache;
        this.portfolioCache = portfolioCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @NotNull public Observable<DisplayablePortfolioDTOList> get(@NotNull UserBaseKey userBaseKey)
    {
        return portfolioListCache.get(userBaseKey)
                .flatMap(this::getDisplayablePortfolios);
    }

    @NotNull protected Observable<DisplayablePortfolioDTOList> getDisplayablePortfolios(@NotNull Pair<UserBaseKey, PortfolioCompactDTOList> pair)
    {
        return getDisplayablePortfolios(pair.first, pair.second);
    }

    @NotNull protected Observable<DisplayablePortfolioDTOList> getDisplayablePortfolios(@NotNull UserBaseKey userBaseKey,
            @NotNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey).map(pair -> pair.second).take(1),
                getPortfolios(portfolioCompactDTOs),
                DisplayablePortfolioDTOList::new);
    }

    @NotNull Observable<PortfolioDTOList> getPortfolios(@NotNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.from(portfolioCompactDTOs)
                .flatMap(portfolioCompact -> portfolioCache.get(portfolioCompact.getOwnedPortfolioId()).take(1))
                .map(pair -> pair.second)
                .toList()
                .map(PortfolioDTOList::new)
                .take(1);
    }
}
