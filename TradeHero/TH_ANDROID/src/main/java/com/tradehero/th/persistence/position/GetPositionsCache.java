package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

@Singleton public class GetPositionsCache extends PartialDTOCache<GetPositionsDTOKey, GetPositionsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<GetPositionsDTOKey, GetPositionsCutDTO> lruCache;
    protected Lazy<PositionServiceWrapper> positionServiceWrapper;
    protected Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    protected Lazy<SecurityCompactCache> securityCompactCache;
    protected Lazy<PortfolioCache> portfolioCache;
    protected Lazy<PositionCache> filedPositionCache;

    //<editor-fold desc="Constructors">

    @Inject public GetPositionsCache(
            Lazy<PositionServiceWrapper> positionServiceWrapper,
            Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            Lazy<SecurityCompactCache> securityCompactCache,
            Lazy<PortfolioCache> portfolioCache,
            Lazy<PositionCache> filedPositionCache)
    {
        this(DEFAULT_MAX_SIZE,
                positionServiceWrapper,
                leaderboardServiceWrapper,
                securityCompactCache,
                portfolioCache,
                filedPositionCache);
    }

    public GetPositionsCache(final int maxSize,
            Lazy<PositionServiceWrapper> positionServiceWrapper,
            Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            Lazy<SecurityCompactCache> securityCompactCache,
            Lazy<PortfolioCache> portfolioCache,
            Lazy<PositionCache> filedPositionCache)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
        this.positionServiceWrapper = positionServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.portfolioCache = portfolioCache;
        this.filedPositionCache = filedPositionCache;
    }
    //</editor-fold>

    protected GetPositionsDTO fetch(final GetPositionsDTOKey key) throws RetrofitError
    {
        if (key instanceof OwnedPortfolioId)
        {
            return this.positionServiceWrapper.get().getPositions((OwnedPortfolioId) key);
        }
        else if (key instanceof LeaderboardMarkUserId)
        {
            return this.leaderboardServiceWrapper.get().getPositionsForLeaderboardMarkUser((LeaderboardMarkUserId) key);
        }
        throw new IllegalArgumentException("Unhandled key type " + key.getClass());
    }

    @Override public GetPositionsDTO get(final GetPositionsDTOKey key)
    {
        final GetPositionsCutDTO getPositionsCutDTO = this.lruCache.get(key);
        if (getPositionsCutDTO == null)
        {
            return null;
        }
        return getPositionsCutDTO.create(
                securityCompactCache.get(),
                filedPositionCache.get());
    }

    @Override public GetPositionsDTO put(final GetPositionsDTOKey key, final GetPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetPositionsDTO previous = null;
        GetPositionsCutDTO previousCut = null;
        previousCut = lruCache.put(
                key,
                new GetPositionsCutDTO(
                        value,
                        securityCompactCache.get(),
                        filedPositionCache.get()));
        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get(), filedPositionCache.get());
        }
        portfolioCache.get().autoFetch((OwnedPortfolioId) key);

        return previous;
    }

    /**
     * Invalidates all the info about the given user
     * @param userBaseKey
     */
    public void invalidate(final UserBaseKey userBaseKey)
    {
        for (GetPositionsDTOKey key : lruCache.snapshot().keySet())
        {
            if (key instanceof OwnedPortfolioId && ((OwnedPortfolioId) key).userId.equals(userBaseKey.key))
            {
                invalidate(key);
            }
            else if (key instanceof LeaderboardMarkUserId)
            {
                throw new IllegalStateException("Unhandled type " + key.getClass());
            }
        }
    }

    @Override public void invalidate(final GetPositionsDTOKey key)
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
        public final List<PositionDTOKey> ownedPositionIds;
        public final List<SecurityId> securityIds;
        public final int openPositionsCount;
        public final int closedPositionsCount;

        public GetPositionsCutDTO(
                GetPositionsDTO getPositionsDTO,
                SecurityCompactCache securityCompactCache,
                PositionCache positionCache)
        {
            positionCache.put(getPositionsDTO.positions);
            this.ownedPositionIds = PositionDTO.getFiledPositionIds(getPositionsDTO.positions);

            securityCompactCache.put(getPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);
            
            this.openPositionsCount = getPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
        }

        public GetPositionsDTO create(
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
