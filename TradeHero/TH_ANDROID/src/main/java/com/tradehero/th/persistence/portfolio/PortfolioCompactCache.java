package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class PortfolioCompactCache extends StraightDTOCacheNew<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public PortfolioCompactDTO fetch(@NotNull PortfolioId key) throws Throwable
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
}