package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
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
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;

@Singleton public class GetPositionsCache extends PartialDTOCache<GetPositionsDTOKey, GetPositionsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull private final THLruCache<GetPositionsDTOKey, GetPositionsCutDTO> lruCache;
    @NotNull private final Lazy<PositionServiceWrapper> positionServiceWrapper;
    @NotNull private final Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    @NotNull private final Lazy<SecurityCompactCache> securityCompactCache;
    @NotNull private final Lazy<PortfolioCache> portfolioCache;
    @NotNull private final Lazy<PositionCache> filedPositionCache;
    @NotNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCache(
            @NotNull Lazy<PositionServiceWrapper> positionServiceWrapper,
            @NotNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            @NotNull Lazy<SecurityCompactCache> securityCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache,
            @NotNull Lazy<PositionCache> filedPositionCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
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
            @NotNull Lazy<PositionServiceWrapper> positionServiceWrapper,
            @NotNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            @NotNull Lazy<SecurityCompactCache> securityCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache,
            @NotNull Lazy<PositionCache> filedPositionCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
        this.positionServiceWrapper = positionServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.portfolioCache = portfolioCache;
        this.filedPositionCache = filedPositionCache;
        this.leaderboardUserCache = leaderboardUserCache;
    }
    //</editor-fold>

    @NotNull
    protected GetPositionsDTO fetch(@NotNull final GetPositionsDTOKey key) throws RetrofitError
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

    @Nullable
    @Override public GetPositionsDTO get(@NotNull final GetPositionsDTOKey key)
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

    @Nullable
    @Override public GetPositionsDTO put(
            @NotNull final GetPositionsDTOKey key,
            @NotNull final GetPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetPositionsDTO previous = null;
        GetPositionsCutDTO previousCut = lruCache.put(
                key,
                new GetPositionsCutDTO(
                        value,
                        securityCompactCache.get(),
                        filedPositionCache.get()));
        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get(), filedPositionCache.get());
        }
        if (key instanceof OwnedPortfolioId)
        {
            portfolioCache.get().autoFetch((OwnedPortfolioId) key);
        }

        return previous;
    }

    /**
     * Invalidates all the info about the given user
     * @param userBaseKey
     */
    public void invalidate(@NotNull final UserBaseKey userBaseKey)
    {
        for (@NotNull GetPositionsDTOKey key : lruCache.snapshot().keySet())
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
        for (@NotNull LeaderboardUserId leaderboardUserId : leaderboardUserCache.get().getAllKeys())
        {
            if (userBaseKey.key == leaderboardUserId.userId)
            {
                invalidate(leaderboardUserId.createLeaderboardMarkUserId());
            }
        }
    }

    @Override public void invalidate(@NotNull final GetPositionsDTOKey key)
    {
        invalidateMatchingPositionCache(get(key));
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    protected void invalidateMatchingPositionCache(@Nullable final GetPositionsDTO value)
    {
        if (value != null && value.positions != null)
        {
            for (@Nullable PositionDTO positionDTO: value.positions)
            {
                if (positionDTO != null)
                {
                    filedPositionCache.get().invalidate(positionDTO.getPositionDTOKey());
                }
            }
        }
    }

    private static class GetPositionsCutDTO
    {
        @Nullable public final List<PositionDTOKey> ownedPositionIds;
        @Nullable public final List<SecurityId> securityIds;
        public final int openPositionsCount;
        public final int closedPositionsCount;

        public GetPositionsCutDTO(
                @NotNull GetPositionsDTO getPositionsDTO,
                @NotNull SecurityCompactCache securityCompactCache,
                @NotNull PositionCache positionCache)
        {
            positionCache.put(getPositionsDTO.positions);
            this.ownedPositionIds = PositionDTO.getFiledPositionIds(getPositionsDTO.positions);

            securityCompactCache.put(getPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);
            
            this.openPositionsCount = getPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
        }

        @NotNull
        public GetPositionsDTO create(
                @NotNull SecurityCompactCache securityCompactCache,
                @NotNull PositionCache positionCache)
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
