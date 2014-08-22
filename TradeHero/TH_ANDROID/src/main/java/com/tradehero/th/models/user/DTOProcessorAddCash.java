package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorAddCash extends DTOProcessorUpdateUserProfile
{
    private final PortfolioCompactListCache portfolioCompactListCache;
    private final PortfolioCompactCache portfolioCompactCache;
    private final PortfolioCache portfolioCache;
    private final OwnedPortfolioId ownedPortfolioId;

    public DTOProcessorAddCash(UserProfileCache userProfileCache,
            PortfolioCompactListCache portfolioCompactListCache,
            PortfolioCompactCache portfolioCompactCache,
            PortfolioCache portfolioCache,
            OwnedPortfolioId ownedPortfolioId)
    {
        super(userProfileCache);
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.ownedPortfolioId = ownedPortfolioId;
    }

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        portfolioCache.invalidate(ownedPortfolioId);
        portfolioCompactCache.invalidate(ownedPortfolioId.getPortfolioIdKey());
        portfolioCompactListCache.getOrFetchAsync(ownedPortfolioId.getUserBaseKey(), true);
        return processed;
    }
}
