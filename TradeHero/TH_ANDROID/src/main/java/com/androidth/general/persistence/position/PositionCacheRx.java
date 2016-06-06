package com.androidth.general.persistence.position;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.android.internal.util.Predicate;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.position.GetPositionsDTO;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.position.OwnedPositionId;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOKey;
import com.androidth.general.api.position.PositionInPeriodDTO;
import com.androidth.general.persistence.leaderboard.position.LeaderboardPositionIdCacheRx;
import com.androidth.general.persistence.trade.TradeListCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class PositionCacheRx extends BaseFetchDTOCacheRx<PositionDTOKey, PositionDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 5000;

    @NonNull protected final Lazy<PositionCompactIdCacheRx> positionCompactIdCache;
    @NonNull protected final Lazy<LeaderboardPositionIdCacheRx> positionIdCache;
    @NonNull protected final Lazy<GetPositionsCacheRx> getPositionsCache;
    @NonNull protected final Lazy<TradeListCacheRx> tradeListCache;

    //<editor-fold desc="Constructors">
    @Inject public PositionCacheRx(
            @NonNull Lazy<PositionCompactIdCacheRx> positionCompactIdCache,
            @NonNull Lazy<LeaderboardPositionIdCacheRx> positionIdCache,
            @NonNull Lazy<GetPositionsCacheRx> getPositionsCache,
            @NonNull Lazy<TradeListCacheRx> tradeListCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.positionCompactIdCache = positionCompactIdCache;
        this.positionIdCache = positionIdCache;
        this.getPositionsCache = getPositionsCache;
        this.tradeListCache = tradeListCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PositionDTO> fetch(@NonNull final PositionDTOKey key)
    {
        GetPositionsDTOKey getPositionsDTOKey;
        if (key instanceof OwnedPositionId)
        {
            getPositionsDTOKey = new OwnedPortfolioId(((OwnedPositionId) key).userId, ((OwnedPositionId) key).portfolioId);
        }
        else if (key instanceof OwnedLeaderboardPositionId)
        {
            getPositionsDTOKey = new LeaderboardMarkUserId(((OwnedLeaderboardPositionId) key).leaderboardMarkUserId);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled PositionDTOKey " + key);
        }
        return getPositionsCache.get().getOne(getPositionsDTOKey)
                .flatMap(new Func1<Pair<GetPositionsDTOKey, GetPositionsDTO>, Observable<PositionDTO>>()
                {
                    @Override public Observable<PositionDTO> call(Pair<GetPositionsDTOKey, GetPositionsDTO> pair)
                    {
                        if (pair.second.positions != null)
                        {
                            PositionDTO position = pair.second.positions.findFirstWhere(new Predicate<PositionDTO>()
                            {
                                @Override public boolean apply(PositionDTO position)
                                {
                                    return position.getPositionDTOKey().equals(key);
                                }
                            });
                            if (position != null)
                            {
                                return Observable.just(position);
                            }
                        }
                        return Observable.error(new IllegalArgumentException("Not found"));
                    }
                });
    }

    @Override public void onNext(@NonNull PositionDTOKey key, @NonNull PositionDTO value)
    {
        // Save the correspondence between integer id and compound key.
        if (key instanceof OwnedPositionId)
        {
            positionCompactIdCache.get().onNext(value.getPositionCompactId(), (OwnedPositionId) key);
        }
        else if (key instanceof OwnedLeaderboardPositionId)
        {
            if (value instanceof PositionInPeriodDTO)
            {
                positionIdCache.get().onNext(((PositionInPeriodDTO) value).getLbPositionId(), (OwnedLeaderboardPositionId) key);
            }
            else
            {
                positionCompactIdCache.get().onNext(value.getPositionCompactId(), value.getOwnedPositionId());
            }
        }
        invalidateMatchingTrades(key);
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

    public void onNext(@NonNull List<PositionDTO> values)
    {
        for (PositionDTO positionDTO: values)
        {
            onNext(positionDTO.getPositionDTOKey(), positionDTO);
        }
    }
}
