package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioId;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PortfolioCompactCache extends StraightDTOCache<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    protected PortfolioCompactDTO fetch(PortfolioId key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch an individual PortfolioCompactDTO");
    }

    @Override public PortfolioCompactDTO put(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value)
    {
        // HACK We need to take care of the bug https://www.pivotaltracker.com/story/show/61190894
        {
            PortfolioCompactDTO current = get(key);
            if (current != null && current.providerId != null)
            {
                value.providerId = current.providerId;
            }
        }

        return super.put(key, value);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PortfolioCompactDTO getFirstByProvider(@NotNull ProviderId providerId)
    {
        for (@NotNull PortfolioCompactDTO portfolioCompactDTO : new ArrayList<>(snapshot().values()))
        {
            if (providerId.equals(portfolioCompactDTO.getProviderIdKey()))
            {
                return portfolioCompactDTO;
            }
        }
        return null;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public PortfolioCompactDTOList get(@Nullable Collection<PortfolioId> portfolioIds)
    {
        if (portfolioIds == null)
        {
            return null;
        }

        PortfolioCompactDTOList portfolioCompactDTOs = new PortfolioCompactDTOList();
        for (@NotNull PortfolioId portfolioId: portfolioIds)
        {
            portfolioCompactDTOs.add(get(portfolioId));
        }
        return portfolioCompactDTOs;
    }
}