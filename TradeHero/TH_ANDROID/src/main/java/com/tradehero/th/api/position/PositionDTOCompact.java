package com.tradehero.th.api.position;

import com.tradehero.th.api.ExtendedDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:27 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompact extends ExtendedDTO
{
    public int id;
    public Integer shares;
    public int portfolioId;

    // This price is always is USD
    public Double averagePriceRefCcy;

    public Boolean isClosed()
    {
        if (shares == null)
        {
            return null;
        }
        return shares == 0;
    }

    public Boolean isOpen()
    {
        if (shares == null)
        {
            return null;
        }
        return shares != 0;
    }

    public PositionCompactId getPositionCompactId()
    {
        return new PositionCompactId(id);
    }

    public static List<PositionCompactId> getPositionCompactIds(List<PositionDTOCompact> positionDTOCompacts)
    {
        if (positionDTOCompacts == null)
        {
            return null;
        }

        List<PositionCompactId> positionCompactIds = new ArrayList<>();
        for (PositionDTOCompact positionDTOCompact: positionDTOCompacts)
        {
            positionCompactIds.add(positionDTOCompact.getPositionCompactId());
        }
        return positionCompactIds;
    }

    @Override public String toString()
    {
        return "PositionDTOCompact{" +
                "id=" + id +
                ", shares=" + shares +
                ", portfolioId=" + portfolioId +
                ", averagePriceRefCcy=" + averagePriceRefCcy +
                ", extras={" + formatExtras(", ").toString() + "}" +
                '}';
    }
}
