package com.tradehero.th.api.portfolio;

import com.tradehero.th.api.competition.ProviderId;
import java.util.HashMap;
import java.util.Map;

public class MappedProviderPortfolioCompactDTO extends HashMap<ProviderId, PortfolioCompactDTOList>
{
    //<editor-fold desc="Constructors">
    public MappedProviderPortfolioCompactDTO(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public MappedProviderPortfolioCompactDTO(int initialCapacity)
    {
        super(initialCapacity);
    }

    public MappedProviderPortfolioCompactDTO()
    {
        super();
    }

    public MappedProviderPortfolioCompactDTO(Map<? extends ProviderId, ? extends PortfolioCompactDTOList> m)
    {
        super(m);
    }
    //</editor-fold>

    public void add(PortfolioCompactDTOList portfolioCompactDTOs)
    {
        if (portfolioCompactDTOs == null)
        {
            return;
        }
        for (PortfolioCompactDTO portfolioCompactDTO: portfolioCompactDTOs)
        {
            add(portfolioCompactDTO);
        }
    }

    public void add(PortfolioCompactDTO portfolioCompactDTO)
    {
        ProviderId providerId = portfolioCompactDTO.getProviderIdKey();
        if (providerId == null)
        {
            return;
        }
        if (!containsKey(providerId))
        {
            put(providerId, new PortfolioCompactDTOList());
        }
        get(providerId).add(portfolioCompactDTO);
    }
}
