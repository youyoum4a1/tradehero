package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.position.OwnedLbPositionId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 1/11/13
 */
@Singleton public class LbPositionCache extends StraightDTOCache<OwnedLbPositionId, PositionInPeriodDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    @Inject Lazy<LbPositionIdCache> positionIdCache;
    @Inject protected Lazy<TradeListCache> tradeListCache;

    @Inject public LbPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected PositionInPeriodDTO fetch(OwnedLbPositionId key)
    {
        throw new IllegalStateException("You should not fetch PositionDTO individually");
    }

    @Override public PositionInPeriodDTO put(OwnedLbPositionId key, PositionInPeriodDTO value)
    {
        // Save the correspondence between integer id and compound key.
        positionIdCache.get().put(value.getLbPositionId(), key);
        invalidateMatchingTrades(key);

        return super.put(key, value);
    }

    @Override public void invalidate(OwnedLbPositionId key)
    {
        invalidateMatchingTrades(key);
        super.invalidate(key);
    }

    protected void invalidateMatchingTrades(OwnedLbPositionId key)
    {
        //tradeListCache.get().invalidate(key);
    }

    public List<PositionInPeriodDTO> put(Integer portfolioId, List<PositionInPeriodDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<PositionInPeriodDTO> previousValues = new ArrayList<>();

        for (PositionInPeriodDTO positionDTO: values)
        {
            previousValues.add(put(positionDTO.getLbOwnedPositionId(portfolioId), positionDTO));
        }

        return previousValues;
    }

    public List<PositionInPeriodDTO> get(List<OwnedLbPositionId> keys)
    {
        if (keys == null)
        {
            return null;
        }

        List<PositionInPeriodDTO> positionDTOs = new ArrayList<>();

        for (OwnedLbPositionId key: keys)
        {
            positionDTOs.add(get(key));
        }

        return positionDTOs;
    }
}

