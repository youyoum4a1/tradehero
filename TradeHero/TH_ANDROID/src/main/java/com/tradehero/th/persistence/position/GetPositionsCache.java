package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:44 PM To change this template use File | Settings | File Templates. */
@Singleton public class GetPositionsCache extends PartialDTOCache<OwnedPortfolioId, GetPositionsDTO>
{
    public static final String TAG = GetPositionsCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<OwnedPortfolioId, GetPositionsCutDTO> lruCache;
    @Inject protected Lazy<PositionServiceWrapper> positionServiceWrapper;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PositionCache> filedPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public GetPositionsCache(final int maxSize)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    protected GetPositionsDTO fetch(final OwnedPortfolioId key) throws RetrofitError
    {
        return this.positionServiceWrapper.get().getPositions(key);
    }

    @Override public GetPositionsDTO get(final OwnedPortfolioId key)
    {
        final GetPositionsCutDTO getPositionsCutDTO = this.lruCache.get(key);
        if (getPositionsCutDTO == null)
        {
            return null;
        }
        return getPositionsCutDTO.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
    }

    @Override public GetPositionsDTO put(final OwnedPortfolioId key, final GetPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetPositionsDTO previous = null;

        GetPositionsCutDTO previousCut = lruCache.put(
                key,
                new GetPositionsCutDTO(
                        value,
                        key.portfolioId,
                        securityCompactCache.get(),
                        filedPositionCache.get()));

        if (previousCut != null)
        {
            previous = previousCut.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
        }

        if (key != null)
        {
            portfolioCache.get().autoFetch(key);
        }

        return previous;
    }

    /**
     * Invalidates all the info about the given user
     * @param userBaseKey
     */
    public void invalidate(final UserBaseKey userBaseKey)
    {
        for (OwnedPortfolioId ownedPortfolioId : lruCache.snapshot().keySet())
        {
            if (ownedPortfolioId.userId.equals(userBaseKey.key))
            {
                invalidate(ownedPortfolioId);
            }
        }
    }

    @Override public void invalidate(final OwnedPortfolioId key)
    {
        invalidateMatchingPositionCache(get(key));
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    protected void invalidateMatchingPositionCache(final GetPositionsDTO value)
    {
        if (value != null && value.positions != null)
        {
            for (PositionDTO positionDTO: value.positions)
            {
                filedPositionCache.get().invalidate(positionDTO.getOwnedPositionId());
            }
        }
    }

    private static class GetPositionsCutDTO
    {
        public final List<OwnedPositionId> ownedPositionIds;
        public final List<SecurityId> securityIds;
        public final int openPositionsCount;
        public final int closedPositionsCount;

        public GetPositionsCutDTO(
                GetPositionsDTO getPositionsDTO,
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                PositionCache positionCache)
        {
            positionCache.put(portfolioId, getPositionsDTO.positions);
            this.ownedPositionIds = PositionDTO.getFiledPositionIds(portfolioId, getPositionsDTO.positions);

            securityCompactCache.put(getPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);
            
            this.openPositionsCount = getPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
        }

        public GetPositionsDTO create(
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                PositionCache positionCache)
        {
            return new GetPositionsDTO(
                    positionCache.get(ownedPositionIds),
                    securityCompactCache.get(securityIds),
                    openPositionsCount,
                    closedPositionsCount
            );
        }
    }
}
