package com.tradehero.th.persistence.position;

import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOFactory;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PositionCache extends StraightDTOCacheNew<PositionDTOKey, PositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    @NotNull protected final Lazy<PositionCompactIdCache> positionCompactIdCache;
    @NotNull protected final Lazy<LeaderboardPositionIdCache> positionIdCache;
    @NotNull protected final Lazy<GetPositionsCache> getPositionsCache;
    @NotNull protected final Lazy<TradeListCache> tradeListCache;
    @NotNull protected final PositionDTOFactory positionDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public PositionCache(
            @NotNull Lazy<PositionCompactIdCache> positionCompactIdCache,
            @NotNull Lazy<LeaderboardPositionIdCache> positionIdCache,
            @NotNull Lazy<GetPositionsCache> getPositionsCache,
            @NotNull Lazy<TradeListCache> tradeListCache,
            @NotNull PositionDTOFactory positionDTOFactory)
    {
        super(DEFAULT_MAX_SIZE);
        this.positionCompactIdCache = positionCompactIdCache;
        this.positionIdCache = positionIdCache;
        this.getPositionsCache = getPositionsCache;
        this.tradeListCache = tradeListCache;
        this.positionDTOFactory = positionDTOFactory;
    }
    //</editor-fold>

    @Override @NotNull public PositionDTO fetch(@NotNull final PositionDTOKey key) throws Throwable
    {
        GetPositionsDTOKey getPositionsDTOKey;
        if (key instanceof OwnedPositionId)
        {
            //getPositionsDTOKey = new OwnedPortfolioId(((OwnedPositionId) key).userId, ((OwnedPositionId) key).portfolioId);
            getPositionsDTOKey = new OwnedPositionId(((OwnedPositionId) key).userId,((OwnedPositionId) key).portfolioId,((OwnedPositionId) key).positionId);
        }
        else if (key instanceof OwnedLeaderboardPositionId)
        {
            getPositionsDTOKey = new LeaderboardMarkUserId(((OwnedLeaderboardPositionId) key).leaderboardMarkUserId);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled PositionDTOKey " + key);
        }
        GetPositionsDTO getPositionsDTO = getPositionsCache.get().getOrFetchSync(getPositionsDTOKey);
        //noinspection ConstantConditions
        return getPositionsDTO.positions.findFirstWhere(new Predicate<PositionDTO>()
        {
            @Override public boolean apply(PositionDTO positionDTO)
            {
                //return positionDTO.getPositionDTOKey().equals(key);
                return positionDTO.getPositionDTOKey().equals(key);
            }
        });
    }

    @Nullable
    @Override public PositionDTO put(@NotNull PositionDTOKey key, @NotNull PositionDTO value)
    {
        // Save the correspondence between integer id and compound key.
        if (key instanceof OwnedPositionId)
        {
            positionCompactIdCache.get().put(value.getPositionCompactId(), (OwnedPositionId) key);
        }
        else if (key instanceof OwnedLeaderboardPositionId)
        {
            if (value instanceof PositionInPeriodDTO)
            {
                positionIdCache.get().put(((PositionInPeriodDTO) value).getLbPositionId(), (OwnedLeaderboardPositionId) key);
            }
            else
            {
                positionCompactIdCache.get().put(value.getPositionCompactId(), value.getOwnedPositionId());
            }
        }
        invalidateMatchingTrades(key);

        return super.put(key, positionDTOFactory.clonePerType(value));
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

    @Contract("null -> null")
    @Nullable
    public PositionDTOList<PositionDTO> put(@Nullable List<PositionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        PositionDTOList<PositionDTO> previousValues = new PositionDTOList<>();

        for (PositionDTO positionDTO: values)
        {
            previousValues.add(put(positionDTO.getPositionDTOKey(), positionDTO));
        }

        return previousValues;
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    public PositionDTOList<PositionDTO> get(@Nullable List<PositionDTOKey> keys)
    {
        if (keys == null)
        {
            return null;
        }

        PositionDTOList<PositionDTO> positionDTOs = new PositionDTOList<>();

        for (PositionDTOKey key: keys)
        {
            positionDTOs.add(get(key));
        }

        return positionDTOs;
    }
}
