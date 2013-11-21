package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import java.util.ArrayList;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 7:43 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompactList extends ArrayList<PositionDTOCompact>
{
    public static final String TAG = PositionDTOCompactList.class.getSimpleName();

    public Integer getMaxSellableShares(PortfolioId portfolioId)
    {
        int total = 0;
        for (PositionDTOCompact positionDTOCompact: this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                total += positionDTOCompact.shares;
            }
        }
        return total;
    }
}
