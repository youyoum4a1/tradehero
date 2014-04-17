package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTOList;
import java.util.Collection;

public class WatchlistPositionDTOList extends PositionDTOList<WatchlistPositionDTO>
{
    //<editor-fold desc="Constructors">
    public WatchlistPositionDTOList(int capacity)
    {
        super(capacity);
    }

    public WatchlistPositionDTOList()
    {
        super();
    }

    public WatchlistPositionDTOList(Collection<? extends WatchlistPositionDTO> collection)
    {
        super(collection);
    }
    //</editor-fold>


}
