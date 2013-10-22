package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 2:35 PM To change this template use File | Settings | File Templates. */
@Singleton public class PositionCompactCache extends StraightDTOCache<PositionCompactId, PositionDTOCompact>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public PositionCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PositionDTOCompact fetch(PositionCompactId key)
    {
        throw new IllegalStateException("You should not fetch PositionDTOCompact");
    }

    public List<PositionDTOCompact> put(List<PositionDTOCompact> values)
    {
        if (values == null)
        {
            return null;
        }

        List<PositionDTOCompact> previousValues = new ArrayList<>();

        for (PositionDTOCompact value: values)
        {
            previousValues.add(put(value.getPositionCompactId(), value));
        }

        return previousValues;
    }

    public List<PositionDTOCompact> get(List<PositionCompactId> positionCompactIds)
    {
        if (positionCompactIds == null)
        {
            return null;
        }
        List<PositionDTOCompact> positionDTOCompacts = new ArrayList<>();

        for (PositionCompactId positionCompactId: positionCompactIds)
        {
            positionDTOCompacts.add(get(positionCompactId));
        }

        return positionDTOCompacts;
    }
}
