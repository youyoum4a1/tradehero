package com.androidth.general.persistence.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.position.GetLivePositionsDTO;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.LeaderboardServiceWrapper;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton @UserCache public class GetLivePositionsCacheRx extends BaseFetchDTOCacheRx<GetPositionsDTOKey, GetLivePositionsDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<Live1BServiceWrapper> live1BServiceWrapper;
    @NonNull private final Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;
    @NonNull private final Lazy<LivePositionCacheRx> positionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetLivePositionsCacheRx(
            @NonNull Lazy<Live1BServiceWrapper> live1BServiceWrapper,
            @NonNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<LivePositionCacheRx> positionCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.live1BServiceWrapper = live1BServiceWrapper;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.positionCache = positionCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<GetLivePositionsDTO> fetch(@NonNull final GetPositionsDTOKey key)
    {
//        if (key instanceof OwnedPortfolioId)
//        {
//            return this.live1BServiceWrapper.get().getPositionsRx((OwnedPortfolioId) key);
//        }
//        else if (key instanceof LeaderboardMarkUserId)
//        {
//            return this.leaderboardServiceWrapper.get().getPositionsForLeaderboardMarkUserRx((LeaderboardMarkUserId) key);
//        }
//        throw new IllegalArgumentException("Unhandled key type " + key.getClass());
//        return this.live1BServiceWrapper.get().getPositions();
        return Observable.empty();
    }

    @Nullable @Override protected GetLivePositionsDTO putValue(@NonNull GetPositionsDTOKey key, @NonNull GetLivePositionsDTO value)
    {
//        if (value.securities != null)
//        {
//            securityCompactCache.get().onNext(value.securities);
//        }
//        if (value.positions != null)
//        {
//            positionCache.get().onNext(value.positions);
//        }
        if(value.response!=null){
            //nothing to cache here
        }
        return super.putValue(key, value);
    }
}
