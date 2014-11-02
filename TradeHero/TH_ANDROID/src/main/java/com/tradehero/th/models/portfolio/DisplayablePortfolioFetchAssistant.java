package com.tradehero.th.models.portfolio;

import android.util.Pair;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @NotNull public Observable<Map<UserBaseKey, DisplayablePortfolioDTOList>> get(@NotNull List<UserBaseKey> userBaseKeys)
    {
        return Observable.from(userBaseKeys)
                .flatMap(userBaseKey -> portfolioListCache.get(userBaseKey).first())
                .flatMap(attributedPortfolios -> Observable.zip(
                        Observable.just(attributedPortfolios.first),
                        getDisplayablePortfolios(attributedPortfolios.first, attributedPortfolios.second).toList(),
                        Pair::create))
                .toList()
                .map(list -> {
                    Map<UserBaseKey, DisplayablePortfolioDTOList> map = new HashMap<>();
                    CollectionUtils.apply(
                            list,
                            element -> map.put(
                                    element.first,
                                    new DisplayablePortfolioDTOList(element.second)));
                    return map;
                });
    }

    @NotNull protected Observable<DisplayablePortfolioDTO> getDisplayablePortfolios(@NotNull UserBaseKey userBaseKey,
            @NotNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return Observable.combineLatest(
                userProfileCache.get(userBaseKey).first(),
                Observable.from(portfolioCompactDTOs)
                        .flatMap(portfolioCompact -> portfolioCache.get(portfolioCompact.getOwnedPortfolioId())
                                .first()),
                (userPair, portfolioPair) -> new DisplayablePortfolioDTO(
                        portfolioPair.first,
                        userPair.second,
                        portfolioPair.second));
    }
}
