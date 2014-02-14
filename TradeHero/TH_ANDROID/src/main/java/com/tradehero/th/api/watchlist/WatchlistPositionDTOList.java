package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTOList;
import java.util.Collection;

/**
 * Created by xavier on 2/14/14.
 */
public class WatchlistPositionDTOList extends PositionDTOList<WatchlistPositionDTO>
{
    public static final String TAG = WatchlistPositionDTOList.class.getSimpleName();

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
