package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:50 PM Copyright (c) TradeHero */
public class WatchlistPositionDTO extends PositionDTO
{
    public Double watchlistPrice;
    public SecurityCompactDTO securityDTO;

    //<editor-fold desc="Constructors">
    public WatchlistPositionDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> WatchlistPositionDTO(ExtendedDTOType other, Class<? extends ExtendedDTO> myClass)
    {
        super(other, myClass);
    }

    public <PositionDTOCompactType extends PositionDTOCompact> WatchlistPositionDTO(PositionDTOCompactType other, Class<? extends PositionDTOCompact> myClass)
    {
        super(other, myClass);
    }

    public <WatchlistPositionDTOType extends WatchlistPositionDTO> WatchlistPositionDTO(
                WatchlistPositionDTOType other, Class<? extends WatchlistPositionDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public Double getWatchlistPrice()
    {
        return (Double) get("watchlistPrice");
    }
}
