package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorAddCash extends DTOProcessorUpdateUserProfile
{
    @NonNull private final PortfolioCompactListCacheRx portfolioCompactListCache;
    @NonNull private final PortfolioCompactCacheRx portfolioCompactCache;
    @NonNull private final PortfolioCacheRx portfolioCache;
    @NonNull private final OwnedPortfolioId ownedPortfolioId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAddCash(@NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
            @NonNull PortfolioCompactCacheRx portfolioCompactCache,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        super(userProfileCache, homeContentCache);
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.ownedPortfolioId = ownedPortfolioId;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        portfolioCache.invalidate(ownedPortfolioId);
        portfolioCompactCache.invalidate(ownedPortfolioId.getPortfolioIdKey());
        portfolioCompactListCache.get(ownedPortfolioId.getUserBaseKey());
        return processed;
    }
}
