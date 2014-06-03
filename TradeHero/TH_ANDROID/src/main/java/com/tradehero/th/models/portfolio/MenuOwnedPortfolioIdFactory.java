package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.portfolio.MappedProviderPortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioIdFactory;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import javax.inject.Inject;

public class MenuOwnedPortfolioIdFactory
{
    private final PortfolioCompactCache portfolioCompactCache;
    private final PortfolioCompactListCache portfolioCompactListCache;
    private final PortfolioIdFactory portfolioIdFactory;
    private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public MenuOwnedPortfolioIdFactory(
            PortfolioCompactCache portfolioCompactCache,
            PortfolioCompactListCache portfolioCompactListCache,
            PortfolioIdFactory portfolioIdFactory,
            CurrentUserId currentUserId)
    {
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioIdFactory = portfolioIdFactory;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    public MenuOwnedPortfolioIdList createProviderPortfolios(
            SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        if (securityPositionDetailDTO == null || securityPositionDetailDTO.providers == null)
        {
            return null;
        }
        ProviderIdList providerIds = securityPositionDetailDTO.providers.getIds();
        MappedProviderPortfolioCompactDTO mappedProviderPortfolioCompactDTO = new MappedProviderPortfolioCompactDTO();
        mappedProviderPortfolioCompactDTO.add(
                portfolioCompactCache.get(
                        portfolioIdFactory.createFrom(
                                portfolioCompactListCache.get(
                                        currentUserId.toUserBaseKey()))));

        MenuOwnedPortfolioIdList menuList = new MenuOwnedPortfolioIdList();
        for (ProviderId providerId : providerIds)
        {
            PortfolioCompactDTOList portfolioCompactDTOs = mappedProviderPortfolioCompactDTO.get(providerId);
            if (portfolioCompactDTOs != null)
            {
                for (PortfolioCompactDTO portfolioCompactDTO : portfolioCompactDTOs)
                {
                    menuList.add(new MenuOwnedPortfolioId(currentUserId.toUserBaseKey(), portfolioCompactDTO));
                }
            }
        }
        return menuList;
    }
}
