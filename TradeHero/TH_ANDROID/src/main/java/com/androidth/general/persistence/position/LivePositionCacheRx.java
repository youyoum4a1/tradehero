package com.androidth.general.persistence.position;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.androidth.general.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.androidth.general.api.live1b.LivePositionDTO;
import com.androidth.general.api.portfolio.LiveOwnedPortfolioId;
import com.androidth.general.api.position.GetLivePositionsDTO;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.position.OwnedPositionId;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOKey;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.persistence.leaderboard.position.LeaderboardPositionIdCacheRx;
import com.androidth.general.persistence.trade.TradeListCacheRx;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class LivePositionCacheRx extends BaseFetchDTOCacheRx<PositionDTOKey, PositionDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 5000;

    @NonNull protected final Lazy<LivePositionCompactIdCacheRx> livePositionCompactIdCache;
    @NonNull protected final Lazy<LeaderboardPositionIdCacheRx> positionIdCache;
    @NonNull protected final Lazy<GetLivePositionsCacheRx> getPositionsLiveCache;
    @NonNull protected final Lazy<TradeListCacheRx> tradeListCache;

    //<editor-fold desc="Constructors">
    @Inject public LivePositionCacheRx(
            @NonNull Lazy<LivePositionCompactIdCacheRx> livePositionCompactIdCache,
            @NonNull Lazy<LeaderboardPositionIdCacheRx> positionIdCache,
            @NonNull Lazy<GetLivePositionsCacheRx> getPositionsLiveCache,
            @NonNull Lazy<TradeListCacheRx> tradeListCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.livePositionCompactIdCache = livePositionCompactIdCache;
        this.positionIdCache = positionIdCache;
        this.getPositionsLiveCache = getPositionsLiveCache;
        this.tradeListCache = tradeListCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PositionDTO> fetch(@NonNull final PositionDTOKey key)
    {
        GetPositionsDTOKey getPositionsDTOKey;
        if (key instanceof OwnedPositionId)
        {
            getPositionsDTOKey = new LiveOwnedPortfolioId(((OwnedPositionId) key).userId, ((OwnedPositionId) key).portfolioId);
        }
//        else if (key instanceof OwnedLeaderboardPositionId)
//        {
//            getPositionsDTOKey = new LeaderboardMarkUserId(((OwnedLeaderboardPositionId) key).leaderboardMarkUserId);
//        }
        else
        {
            throw new IllegalArgumentException("Unhandled PositionDTOKey " + key);
        }
        return getPositionsLiveCache.get().getOne(getPositionsDTOKey)
                .flatMap(new Func1<Pair<GetPositionsDTOKey, GetLivePositionsDTO>, Observable<PositionDTO>>()
                {
                    @Override public Observable<PositionDTO> call(Pair<GetPositionsDTOKey, GetLivePositionsDTO> pair)
                    {
//                        if (pair.second.positions != null)
//                        {
////                            LivePositionDTO position = pair.second.response.findFirstWhere(new Predicate<LivePositionDTO>()
////                            {
////                                @Override public boolean apply(LivePositionDTO position)
////                                {
////                                    return position.getLivePositionDTOKey().equals(key);
////                                }
////                            });
////                            if (position != null)
////                            {
////                                return Observable.just(position);
////                            }
//                            LivePositionDTO position = pair.second.positions.get(0);
//                            if (position != null)
//                            {
//                                return Observable.just(position);
//                            }
//                        }
                        return Observable.error(new IllegalArgumentException("Not found"));

                    }
                });
    }

    @Override public void onNext(@NonNull PositionDTOKey key, @NonNull PositionDTO value)
    {
//        // Save the correspondence between integer id and compound key.
//        if (key instanceof LiveOwnedPositionId)
//        {
//            livePositionCompactIdCache.get().onNext(value.getPositionCompactId(), (OwnedPositionId) key);
//        }
//        else if (key instanceof OwnedLeaderboardPositionId)
//        {
//            if (value instanceof PositionInPeriodDTO)
//            {
//                positionIdCache.get().onNext(((PositionInPeriodDTO) value).getLbPositionId(), (OwnedLeaderboardPositionId) key);
//            }
//            else
//            {
//                positionCompactIdCache.get().onNext(value.getPositionCompactId(), value.getOwnedPositionId());
//            }
//        }
//        invalidateMatchingTrades(key);
        super.onNext(key, value);
    }

    @Override public void invalidate(@NonNull PositionDTOKey key)
    {
        invalidateMatchingTrades(key);
        super.invalidate(key);
    }

    protected void invalidateMatchingTrades(@NonNull PositionDTOKey key)
    {
        if (key instanceof OwnedPositionId)
        {
            tradeListCache.get().invalidate((OwnedPositionId) key);
        }
        else if (key instanceof OwnedLeaderboardPositionId)
        {
            // TODO
        }
        else
        {
            throw new IllegalArgumentException("Unhandled key type " + key.getClass());
        }
    }

    public void onNext(@NonNull List<LivePositionDTO> values)
    {
//        for (LivePositionDTO positionDTO: values)
//        {
//            onNext(positionDTO.getPositionDTOKey(), positionDTO);
//        }
    }
}
