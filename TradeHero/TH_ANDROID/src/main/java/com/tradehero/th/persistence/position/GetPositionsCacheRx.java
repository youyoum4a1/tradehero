package com.ayondo.academy.persistence.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.position.GetPositionsDTO;
import com.ayondo.academy.api.position.GetPositionsDTOKey;
import com.ayondo.academy.network.service.LeaderboardServiceWrapper;
import com.ayondo.academy.network.service.PositionServiceWrapper;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache public class GetPositionsCacheRx extends BaseFetchDTOCacheRx<GetPositionsDTOKey, GetPositionsDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<PositionServiceWrapper> positionServiceWrapper;
    @NonNull private final Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;
    @NonNull private final Lazy<PositionCacheRx> positionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCacheRx(
            @NonNull Lazy<PositionServiceWrapper> positionServiceWrapper,
            @NonNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<PositionCacheRx> positionCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.positionServiceWrapper = positionServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.positionCache = positionCache;
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

    @Nullable @Override protected GetPositionsDTO putValue(@NonNull GetPositionsDTOKey key, @NonNull GetPositionsDTO value)
    {
        if (value.securities != null)
        {
            securityCompactCache.get().onNext(value.securities);
        }
        if (value.positions != null)
        {
            positionCache.get().onNext(value.positions);
        }
        return super.putValue(key, value);
    }
}
