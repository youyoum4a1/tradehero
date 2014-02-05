package com.tradehero.th.api.position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by xavier on 2/5/14.
 */
public class PositionDTOList<PositionDTOType extends PositionDTO>
        extends ArrayList<PositionDTOType>
{
    public static final String TAG = PositionDTOList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public PositionDTOList(int capacity)
    {
        super(capacity);
    }

    public PositionDTOList()
    {
        super();
    }

    public PositionDTOList(Collection<? extends PositionDTOType> collection)
    {
        super(collection);
    }
    //</editor-fold>

    public Date getEarliestTradeUtc()
    {
        Date earliest = null;

        for (PositionDTOType positionDTO : this)
        {
            if (positionDTO != null && positionDTO.earliestTradeUtc != null)
            {
                if (earliest == null || positionDTO.earliestTradeUtc.before(earliest))
                {
                    earliest = positionDTO.earliestTradeUtc;
                }
            }
        }

        return earliest;
    }

    public Date getLatestTradeUtc()
    {
        Date latest = null;

        for (PositionDTOType positionDTO : this)
        {
            if (positionDTO != null && positionDTO.latestTradeUtc != null)
            {
                if (latest == null || positionDTO.latestTradeUtc.after(latest))
                {
                    latest = positionDTO.latestTradeUtc;
                }
            }
        }

        return latest;
    }
}
