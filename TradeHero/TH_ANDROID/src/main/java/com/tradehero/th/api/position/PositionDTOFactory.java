package com.tradehero.th.api.position;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/13/14.
 */
@Singleton public class PositionDTOFactory
{
    public static final String TAG = PositionDTOFactory.class.getSimpleName();

    public static final String WATCHLIST_PRICE_FIELD = "watchlistPrice";
    public static final String WATCHLIST_SECURITY_FIELD = "securityDTO";

    @Inject public PositionDTOFactory()
    {
        super();
    }

    public PositionDTO clonePerType(PositionDTO positionDTO)
    {
        PositionDTO returned = positionDTO;
        if (positionDTO == null)
        {
            // Nothing to do
        }
        else if (isWatchlistPositionDTO(positionDTO))
        {
            if (!(positionDTO instanceof WatchlistPositionDTO))
            {
                positionDTO = new WatchlistPositionDTO(positionDTO, WatchlistPositionDTO.class);
            }
        }
        // TODO other types

        return positionDTO;
    }

    public List<PositionDTO> clonePerType(List<PositionDTO> list)
    {
        if (list == null)
        {
            return null;
        }

        List<PositionDTO> returned = new ArrayList<>();

        for (PositionDTO positionDTO : list)
        {
            returned.add(clonePerType(positionDTO));
        }

        return returned;
    }

    public boolean isWatchlistPositionDTO(PositionDTO positionDTO)
    {
        return positionDTO != null && positionDTO.get(WATCHLIST_PRICE_FIELD) != null;
    }
}
