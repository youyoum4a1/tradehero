package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.portfolio.PortfolioId;
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

@Singleton public class PositionCache extends StraightDTOCache<PositionDTOKey, PositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    protected Lazy<PositionCompactIdCache> positionCompactIdCache;
    protected Lazy<LeaderboardPositionIdCache> positionIdCache;
    protected Lazy<TradeListCache> tradeListCache;
    protected PositionDTOFactory positionDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public PositionCache(
            Lazy<PositionCompactIdCache> positionCompactIdCache,
            Lazy<LeaderboardPositionIdCache> positionIdCache,
            Lazy<TradeListCache> tradeListCache,
            PositionDTOFactory positionDTOFactory)
    {
        super(DEFAULT_MAX_SIZE);
        this.positionCompactIdCache = positionCompactIdCache;
        this.positionIdCache = positionIdCache;
        this.tradeListCache = tradeListCache;
        this.positionDTOFactory = positionDTOFactory;
    }
    //</editor-fold>

    @Override protected PositionDTO fetch(PositionDTOKey key)
    {
        throw new IllegalStateException("You should not fetch PositionDTO individually");
    }

    @Override public PositionDTO put(PositionDTOKey key, PositionDTO value)
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

    @Override public void invalidate(PositionDTOKey key)
    {
        invalidateMatchingTrades(key);
        super.invalidate(key);
    }

    protected void invalidateMatchingTrades(PositionDTOKey key)
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

    public PositionDTOList<PositionDTO> put(List<PositionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        PositionDTOList<PositionDTO> previousValues = new PositionDTOList<>();

        for (PositionDTO positionDTO: values)
        {
            previousValues.add(put(positionDTO.getOwnedPositionId(), positionDTO));
        }

        return previousValues;
    }

    public PositionDTOList<PositionDTO> get(List<PositionDTOKey> keys)
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
