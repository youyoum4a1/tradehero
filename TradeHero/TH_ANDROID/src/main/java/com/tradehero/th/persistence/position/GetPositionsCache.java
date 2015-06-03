package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetPositionsCache extends StraightCutDTOCacheNew<GetPositionsDTOKey, GetPositionsDTO, GetPositionsCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

     private final Lazy<PositionServiceWrapper> positionServiceWrapper;
     private final Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
     private final Lazy<SecurityCompactCache> securityCompactCache;
     private final Lazy<PortfolioCache> portfolioCache;
     private final Lazy<PositionCache> filedPositionCache;
     private final Lazy<LeaderboardUserCache> leaderboardUserCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCache(
             Lazy<PositionServiceWrapper> positionServiceWrapper,
             Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
             Lazy<SecurityCompactCache> securityCompactCache,
             Lazy<PortfolioCache> portfolioCache,
             Lazy<PositionCache> filedPositionCache,
             Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        this(DEFAULT_MAX_SIZE,
                positionServiceWrapper,
                leaderboardServiceWrapper,
                securityCompactCache,
                portfolioCache,
                filedPositionCache,
                leaderboardUserCache);
    }

    public GetPositionsCache(final int maxSize,
             Lazy<PositionServiceWrapper> positionServiceWrapper,
             Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
             Lazy<SecurityCompactCache> securityCompactCache,
             Lazy<PortfolioCache> portfolioCache,
             Lazy<PositionCache> filedPositionCache,
             Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        super(maxSize);
        this.positionServiceWrapper = positionServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.portfolioCache = portfolioCache;
        this.filedPositionCache = filedPositionCache;
        this.leaderboardUserCache = leaderboardUserCache;
    }
    //</editor-fold>

    @Override  public GetPositionsDTO fetch( final GetPositionsDTOKey key) throws Throwable
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

     @Override protected GetPositionsCutDTO cutValue( GetPositionsDTOKey key,  GetPositionsDTO value)
    {
        return new GetPositionsCutDTO(value, securityCompactCache.get(), filedPositionCache.get());
    }

    @Override protected GetPositionsDTO inflateValue( GetPositionsDTOKey key, GetPositionsCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(securityCompactCache.get(), filedPositionCache.get());
    }

    @Override public GetPositionsDTO put(
             final GetPositionsDTOKey key,
             final GetPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetPositionsDTO previous = super.put(key, value);

        if (key instanceof OwnedPortfolioId)
        {
            portfolioCache.get().getOrFetchAsync((OwnedPortfolioId) key);
        }

        return previous;
    }

    /**
     * Invalidates all the info about the given user
     * @param userBaseKey
     */
    public void invalidate( final UserBaseKey userBaseKey)
    {
        for ( GetPositionsDTOKey key : snapshot().keySet())
        {
            if (key instanceof OwnedPortfolioId && ((OwnedPortfolioId) key).userId.equals(userBaseKey.key))
            {
                invalidate(key);
            }
            else if (key instanceof LeaderboardMarkUserId)
            {
                // Nothing to do
            }
        }

        // Below is an attempt to find out more about this user. It is not 100%
        // fail-safe
        for ( LeaderboardUserId leaderboardUserId : leaderboardUserCache.get().getAllKeys())
        {
            if (userBaseKey.key == leaderboardUserId.userId)
            {
                invalidate(leaderboardUserId.createLeaderboardMarkUserId());
            }
        }
    }

    @Override public void invalidate( final GetPositionsDTOKey key)
    {
        invalidateMatchingPositionCache(get(key));
        super.invalidate(key);
    }

    protected void invalidateMatchingPositionCache(final GetPositionsDTO value)
    {
        if (value != null && value.positions != null)
        {
            for (PositionDTO positionDTO: value.positions)
            {
                if (positionDTO != null)
                {
                    filedPositionCache.get().invalidate(positionDTO.getPositionDTOKey());
                }
            }
        }
    }
}
