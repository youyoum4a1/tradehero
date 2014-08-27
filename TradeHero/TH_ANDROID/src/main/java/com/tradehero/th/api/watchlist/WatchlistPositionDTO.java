package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.Nullable;

public class WatchlistPositionDTO extends PositionDTO
{
    public static final String WATCHLIST_PRICE_FIELD = "watchlistPrice";

    @Nullable public Double watchlistPrice;
    @Nullable public SecurityCompactDTO securityDTO;

    //<editor-fold desc="Constructors">
    public WatchlistPositionDTO()
    {
        super();
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
