package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import javax.inject.Inject;

public class MenuOwnedPortfolioIdFactory
{
    private final PortfolioCompactCache portfolioCompactCache;
    private final PortfolioCompactListCache portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public MenuOwnedPortfolioIdFactory(
            PortfolioCompactCache portfolioCompactCache,
            PortfolioCompactListCache portfolioCompactListCache)
    {
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    public MenuOwnedPortfolioIdList createPortfolioMenus(
            UserBaseKey forUser,
            SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        MenuOwnedPortfolioIdList menus = new MenuOwnedPortfolioIdList();
        menus.add(createMainPortfolioMenu(forUser));
        MenuOwnedPortfolioIdList providerMenus = createProviderPortfolioMenus(forUser, securityPositionDetailDTO);
        if (providerMenus != null)
        {
            menus.addAll(providerMenus);
        }
        return menus;
    }

    public MenuOwnedPortfolioId createMainPortfolioMenu(UserBaseKey forUser)
    {
        OwnedPortfolioId mainPortfolioId = portfolioCompactListCache.getDefaultPortfolio(forUser);
        if (mainPortfolioId == null)
        {
            return null;
        }
        PortfolioCompactDTO mainPortfolio = portfolioCompactCache.get(mainPortfolioId.getPortfolioIdKey());
        if (mainPortfolio == null)
        {
            return null;
        }
        return new MenuOwnedPortfolioId(mainPortfolioId, mainPortfolio);
    }

    public MenuOwnedPortfolioIdList createPortfolioMenus(OwnedPortfolioIdList ownedPortfolioIds)
    {
        if (ownedPortfolioIds == null)
        {
            return null;
        }
        MenuOwnedPortfolioIdList menuOwnedPortfolioIds = new MenuOwnedPortfolioIdList();
        for (OwnedPortfolioId ownedPortfolioId : ownedPortfolioIds)
        {
            menuOwnedPortfolioIds.add(new MenuOwnedPortfolioId(
                    ownedPortfolioId,
                    portfolioCompactCache.get(ownedPortfolioId.getPortfolioIdKey())));
        }
        return menuOwnedPortfolioIds;
    }

    public MenuOwnedPortfolioIdList createProviderPortfolioMenus(
            UserBaseKey forUser,
            SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        if (securityPositionDetailDTO == null || forUser == null)
        {
            return null;
        }

        return createPortfolioMenus(securityPositionDetailDTO.getProviderAssociatedOwnedPortfolioIds(forUser));
    }
}
