package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorAddCash extends DTOProcessorUpdateUserProfile
{
    @NotNull private final PortfolioCompactListCache portfolioCompactListCache;
    @NotNull private final PortfolioCompactCache portfolioCompactCache;
    @NotNull private final PortfolioCache portfolioCache;
    @NotNull private final OwnedPortfolioId ownedPortfolioId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAddCash(@NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull PortfolioCompactCache portfolioCompactCache,
            @NotNull PortfolioCache portfolioCache,
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
        portfolioCompactListCache.getOrFetchAsync(ownedPortfolioId.getUserBaseKey(), true);
        return processed;
    }
}
