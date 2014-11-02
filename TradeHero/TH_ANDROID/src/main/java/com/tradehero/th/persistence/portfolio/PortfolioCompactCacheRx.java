package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class PortfolioCompactCacheRx extends BaseDTOCacheRx<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 20;

    // Giant HACK to survive invalidation #77003578
    private final Map<PortfolioId, Double> txnCostUsds;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.txnCostUsds = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onNext(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value)
    {
        PortfolioCompactDTO previous = getValue(key);
        //noinspection ConstantConditions
        if (previous != null && previous.userId != null)
        {
            value.userId = previous.userId;
        }
        //noinspection ConstantConditions
        if (value.userId == null)
        {
            throw new NullPointerException("UserId should be set");
        }

        // HACK We need to take care of the bug https://www.pivotaltracker.com/story/show/61190894
        {
            if (previous != null && previous.providerId != null)
            {
                value.providerId = previous.providerId;
            }
        }
        // HACK We need to do this while the txnCostUsd may not always be passed
        {
            Double txnCostUsd = txnCostUsds.get(key);
            if (value.txnCostUsd != null)
            {
                txnCostUsds.put(key, value.txnCostUsd);
            }
            else if (txnCostUsd != null)
            {
                value.txnCostUsd = txnCostUsd;
            }
        }

        super.onNext(key, value);
    }

    public void onNext(@NotNull List<PortfolioCompactDTO> portfolioCompactDTOs)
    {
        for (@NotNull PortfolioCompactDTO portfolioCompactDTO : portfolioCompactDTOs)
        {
            onNext(portfolioCompactDTO.getPortfolioId(), portfolioCompactDTO);
        }
    }

    public void invalidate(@NotNull UserBaseKey concernedUser)
    {
        invalidate(concernedUser, false);
    }

    public void invalidate(@NotNull UserBaseKey concernedUser, boolean onlyWatchlist)
    {
        PortfolioCompactDTO cached;
        for (PortfolioId key: snapshot().keySet())
        {
            cached = getValue(key);
            if (cached != null
                    && cached.userId.equals(concernedUser.key)
                    && (cached.isWatchlist || !onlyWatchlist))
            {
                invalidate(cached.getPortfolioId());
            }
        }
    }
}