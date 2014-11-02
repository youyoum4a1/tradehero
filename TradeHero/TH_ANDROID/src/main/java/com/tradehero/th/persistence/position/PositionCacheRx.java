package com.tradehero.th.persistence.position;

import android.util.Pair;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCacheRx;
import com.tradehero.th.persistence.trade.TradeListCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class PositionCacheRx extends BaseFetchDTOCacheRx<PositionDTOKey, PositionDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 5000;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 50;

    @NotNull protected final Lazy<PositionCompactIdCacheRx> positionCompactIdCache;
    @NotNull protected final Lazy<LeaderboardPositionIdCacheRx> positionIdCache;
    @NotNull protected final Lazy<GetPositionsCacheRx> getPositionsCache;
    @NotNull protected final Lazy<TradeListCacheRx> tradeListCache;

    //<editor-fold desc="Constructors">
    @Inject public PositionCacheRx(
            @NotNull Lazy<PositionCompactIdCacheRx> positionCompactIdCache,
            @NotNull Lazy<LeaderboardPositionIdCacheRx> positionIdCache,
            @NotNull Lazy<GetPositionsCacheRx> getPositionsCache,
            @NotNull Lazy<TradeListCacheRx> tradeListCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.positionCompactIdCache = positionCompactIdCache;
        this.positionIdCache = positionIdCache;
        this.getPositionsCache = getPositionsCache;
        this.tradeListCache = tradeListCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<PositionDTO> fetch(@NotNull final PositionDTOKey key)
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
        Observable<Pair<GetPositionsDTOKey, GetPositionsDTO>> getPositionsDTO = getPositionsCache.get().get(getPositionsDTOKey);
        return getPositionsDTO
                .map(pair -> pair.second.positions)
                .flatMap(Observable::from)
                .first(position -> position.getPositionDTOKey().equals(key));
    }

    @Override public void onNext(@NotNull PositionDTOKey key, @NotNull PositionDTO value)
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
        invalidateMatchingTrades(key); super.onNext(key, value);
    }

    @Override public void invalidate(@NotNull PositionDTOKey key)
    {
        invalidateMatchingTrades(key);
        super.invalidate(key);
    }

    protected void invalidateMatchingTrades(@NotNull PositionDTOKey key)
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

    public void onNext(@NotNull List<PositionDTO> values)
    {
        for (PositionDTO positionDTO: values)
        {
            onNext(positionDTO.getPositionDTOKey(), positionDTO);
        }
    }
}
