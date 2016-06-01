package com.ayondo.academy.persistence.portfolio;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class PortfolioCompactCacheRx extends BaseDTOCacheRx<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;

    // Giant HACK to survive invalidation #77003578
    private final Map<PortfolioId, Double> txnCostUsds;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.txnCostUsds = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onNext(@NonNull PortfolioId key, @NonNull PortfolioCompactDTO value)
    {
        PortfolioCompactDTO previous = getCachedValue(key);
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

    public void onNext(@NonNull List<PortfolioCompactDTO> portfolioCompactDTOs)
    {
        for (PortfolioCompactDTO portfolioCompactDTO : portfolioCompactDTOs)
        {
            onNext(portfolioCompactDTO.getPortfolioId(), portfolioCompactDTO);
        }
    }
}