package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class WatchlistPositionDTO extends PositionDTO
{
    public static final String WATCHLIST_PRICE_FIELD = "watchlistPrice";

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

    @Override public String toString()
    {
        return "WatchlistPositionDTO{" +
                "watchlistPrice=" + watchlistPrice +
                ", securityDTO=" + securityDTO +
                '}';
    }
}
