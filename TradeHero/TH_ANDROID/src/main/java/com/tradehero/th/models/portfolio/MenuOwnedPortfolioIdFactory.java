package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuOwnedPortfolioIdFactory
{
    @NotNull private final PortfolioCompactCache portfolioCompactCache;
    @NotNull private final PortfolioCompactListCache portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public MenuOwnedPortfolioIdFactory(
            @NotNull PortfolioCompactCache portfolioCompactCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache)
    {
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @NotNull public MenuOwnedPortfolioIdList createPortfolioMenus(
            @NotNull UserBaseKey forUser,
            @Nullable SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        MenuOwnedPortfolioIdList menus = new MenuOwnedPortfolioIdList();
        MenuOwnedPortfolioId mainPortfolioMenu = createMainPortfolioMenu(forUser);
        if (mainPortfolioMenu != null)
        {
            menus.add(createMainPortfolioMenu(forUser));
        }
        MenuOwnedPortfolioIdList providerMenus = createProviderPortfolioMenus(forUser, securityPositionDetailDTO);
        if (providerMenus != null)
        {
            menus.addAll(providerMenus);
        }
        return menus;
    }

    @Nullable public MenuOwnedPortfolioId createMainPortfolioMenu(@NotNull UserBaseKey forUser)
    {
        PortfolioCompactDTO mainPortfolio = portfolioCompactListCache.getDefaultPortfolio(forUser);
        if (mainPortfolio == null)
        {
            return null;
        }
        return new MenuOwnedPortfolioId(mainPortfolio.getUserBaseKey(), mainPortfolio);
    }

    @Nullable public MenuOwnedPortfolioIdList createPortfolioMenus(@Nullable OwnedPortfolioIdList ownedPortfolioIds)
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

    @Nullable public MenuOwnedPortfolioIdList createProviderPortfolioMenus(
            @NotNull UserBaseKey forUser,
            @Nullable SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        if (securityPositionDetailDTO == null)
        {
            return null;
        }

        return createPortfolioMenus(securityPositionDetailDTO.getProviderAssociatedOwnedPortfolioIds(forUser));
    }
}
