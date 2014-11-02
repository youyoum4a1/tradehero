package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorAddCash extends DTOProcessorUpdateUserProfile
{
    @NotNull private final PortfolioCompactListCacheRx portfolioCompactListCache;
    @NotNull private final PortfolioCompactCacheRx portfolioCompactCache;
    @NotNull private final PortfolioCacheRx portfolioCache;
    @NotNull private final OwnedPortfolioId ownedPortfolioId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAddCash(@NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull PortfolioCompactListCacheRx portfolioCompactListCache,
            @NotNull PortfolioCompactCacheRx portfolioCompactCache,
            @NotNull PortfolioCacheRx portfolioCache,
            @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        super(userProfileCache, homeContentCache);
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.ownedPortfolioId = ownedPortfolioId;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        portfolioCache.invalidate(ownedPortfolioId);
        portfolioCompactCache.invalidate(ownedPortfolioId.getPortfolioIdKey());
        portfolioCompactListCache.get(ownedPortfolioId.getUserBaseKey());
        return processed;
    }
}
