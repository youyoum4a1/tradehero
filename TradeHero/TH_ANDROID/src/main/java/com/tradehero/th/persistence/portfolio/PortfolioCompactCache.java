package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PortfolioCompactCache extends StraightDTOCacheNew<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public PortfolioCompactDTO fetch(@NotNull PortfolioId key) throws Throwable
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

        return super.put(key, value);
    }

    @NotNull public PortfolioCompactDTOList put(@NotNull List<PortfolioCompactDTO> portfolioCompactDTOs)
    {
        PortfolioCompactDTOList previous = new PortfolioCompactDTOList();
        for (@NotNull PortfolioCompactDTO portfolioCompactDTO : portfolioCompactDTOs)
        {
            previous.add(put(portfolioCompactDTO.getPortfolioId(), portfolioCompactDTO));
        }
        return previous;
    }

    @NotNull public PortfolioCompactDTOList get(@NotNull List<PortfolioId> portfolioIds, @Nullable PortfolioId typeQualifier)
    {
        PortfolioCompactDTOList previous = new PortfolioCompactDTOList();
        for (@NotNull PortfolioId portfolioId : portfolioIds)
        {
            previous.add(get(portfolioId));
        }
        return previous;
    }

    @NotNull public PortfolioCompactDTOList get(@NotNull List<OwnedPortfolioId> ownedPortfolioIds, @Nullable OwnedPortfolioId typeQualifier)
    {
        PortfolioCompactDTOList previous = new PortfolioCompactDTOList();
        for (@NotNull OwnedPortfolioId ownedPortfolioId : ownedPortfolioIds)
        {
            previous.add(get(ownedPortfolioId.getPortfolioIdKey()));
        }
        return previous;
    }
}