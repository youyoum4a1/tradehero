package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache
public class PortfolioCompactCache extends StraightDTOCacheNew<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    // Giant HACK to survive invalidation #77003578
    private final Map<PortfolioId, Double> txnCostUsds;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.txnCostUsds = new HashMap<>();
    }
    //</editor-fold>

    @Override @NotNull public PortfolioCompactDTO fetch(@NotNull PortfolioId key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch an individual PortfolioCompactDTO");
    }

    @Override public PortfolioCompactDTO put(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value)
    {
        PortfolioCompactDTO previous = get(key);
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
            PortfolioCompactDTO current = get(key);
            if (current != null && current.providerId != null)
            {
                value.providerId = current.providerId;
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

    public void invalidate(@NotNull UserBaseKey concernedUser)
    {
        invalidate(concernedUser, false);
    }

    public void invalidate(@NotNull UserBaseKey concernedUser, boolean onlyWatchlist)
    {
        PortfolioCompactDTO cached;
        for (PortfolioId key: snapshot().keySet())
        {
            cached = get(key);
            if (cached != null
                    && cached.userId.equals(concernedUser.key)
                    && (cached.isWatchlist || !onlyWatchlist))
            {
                invalidate(cached.getPortfolioId());
            }
        }
    }
}