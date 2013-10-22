package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class PositionCache extends StraightDTOCache<String, OwnedPositionId, PositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 5000;

    @Inject Lazy<PositionCompactIdCache> positionCompactIdCache;

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

        return super.put(key, value);
    }

    public List<PositionDTO> put(Integer portfolioId, List<PositionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<PositionDTO> previousValues = new ArrayList<>();

        for (PositionDTO positionDTO: values)
        {
            previousValues.add(put(positionDTO.getFiledPositionId(portfolioId), positionDTO));
        }

        return previousValues;
    }

    public List<PositionDTO> get(List<OwnedPositionId> keys)
    {
        if (keys == null)
        {
            return null;
        }

        List<PositionDTO> positionDTOs = new ArrayList<>();

        for (OwnedPositionId key: keys)
        {
            positionDTOs.add(get(key));
        }

        return positionDTOs;
    }
}
