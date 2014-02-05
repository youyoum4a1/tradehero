package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class PositionCache extends StraightDTOCache<OwnedPositionId, PositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    @Inject Lazy<PositionCompactIdCache> positionCompactIdCache;
    @Inject protected Lazy<TradeListCache> tradeListCache;

    //<editor-fold desc="Constructors">
    @Inject public PositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PositionDTO fetch(OwnedPositionId key)
    {
        throw new IllegalStateException("You should not fetch PositionDTO individually");
    }

    @Override public PositionDTO put(OwnedPositionId key, PositionDTO value)
    {
        // Save the correspondence between integer id and compound key.
        positionCompactIdCache.get().put(value.getPositionCompactId(), key);
        invalidateMatchingTrades(key);

        return super.put(key, value);
    }

    @Override public void invalidate(OwnedPositionId key)
    {
        invalidateMatchingTrades(key);
        super.invalidate(key);
    }

    protected void invalidateMatchingTrades(OwnedPositionId key)
    {
        tradeListCache.get().invalidate(key);
    }

    public PositionDTOList<PositionDTO> put(Integer portfolioId, List<PositionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        PositionDTOList<PositionDTO> previousValues = new PositionDTOList<>();

        for (PositionDTO positionDTO: values)
        {
            previousValues.add(put(positionDTO.getOwnedPositionId(portfolioId), positionDTO));
        }

        return previousValues;
    }

    public PositionDTOList<PositionDTO> get(List<OwnedPositionId> keys)
    {
        if (keys == null)
        {
            return null;
        }

        PositionDTOList<PositionDTO> positionDTOs = new PositionDTOList<>();

        for (OwnedPositionId key: keys)
        {
            positionDTOs.add(get(key));
        }

        return positionDTOs;
    }
}
