package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;

@Singleton @UserCache public class GetPositionsCacheRx extends BaseFetchDTOCacheRx<GetPositionsDTOKey, GetPositionsDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final Lazy<PositionServiceWrapper> positionServiceWrapper;
    @NonNull private final Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull private final Lazy<PositionCacheRx> filedPositionCache;
    @NonNull private final Lazy<LeaderboardUserCacheRx> leaderboardUserCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCacheRx(
            @NonNull Lazy<PositionServiceWrapper> positionServiceWrapper,
            @NonNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Lazy<PositionCacheRx> filedPositionCache,
            @NonNull Lazy<LeaderboardUserCacheRx> leaderboardUserCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.positionServiceWrapper = positionServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.portfolioCache = portfolioCache;
        this.filedPositionCache = filedPositionCache;
        this.leaderboardUserCache = leaderboardUserCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<GetPositionsDTO> fetch(@NonNull final GetPositionsDTOKey key)
    {
        if (key instanceof OwnedPortfolioId)
        {
            return this.positionServiceWrapper.get().getPositionsRx((OwnedPortfolioId) key);
        }
        else if (key instanceof LeaderboardMarkUserId)
        {
            return this.leaderboardServiceWrapper.get().getPositionsForLeaderboardMarkUserRx((LeaderboardMarkUserId) key);
        }
        throw new IllegalArgumentException("Unhandled key type " + key.getClass());
    }

    @Override public void onNext(@NonNull GetPositionsDTOKey key, @NonNull GetPositionsDTO value)
    {
        if (value.securities != null)
        {
            securityCompactCache.get().onNext(value.securities);
        }
        if (value.positions != null)
        {
            filedPositionCache.get().onNext(value.positions);
        }
        invalidateMatchingPositionCache(getValue(key));

        if (key instanceof OwnedPortfolioId)
        {
            portfolioCache.get().get((OwnedPortfolioId) key);
        }

        super.onNext(key, value);
    }

    /**
     * Invalidates all the info about the given user
     * @param userBaseKey
     */
    public void invalidate(@NonNull final UserBaseKey userBaseKey)
    {
        for (GetPositionsDTOKey key : snapshot().keySet())
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
        for (LeaderboardUserId leaderboardUserId : leaderboardUserCache.get().getAllKeys())
        {
            if (userBaseKey.key == leaderboardUserId.userId)
            {
                invalidate(leaderboardUserId.createLeaderboardMarkUserId());
            }
        }
    }

    @Override public void invalidate(@NonNull final GetPositionsDTOKey key)
    {
        invalidateMatchingPositionCache(getValue(key));
        super.invalidate(key);
    }

    protected void invalidateMatchingPositionCache(@Nullable final GetPositionsDTO value)
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
