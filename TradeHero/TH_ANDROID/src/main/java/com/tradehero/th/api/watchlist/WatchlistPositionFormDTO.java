package com.tradehero.th.api.watchlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import android.support.annotation.NonNull;

public class WatchlistPositionFormDTO
{
    public int securityId;
    public double price;
    public int quantity;

    //<editor-fold desc="Constructors">
    public WatchlistPositionFormDTO(int securityId, double price, int quantity)
    {
        this.securityId = securityId;
        this.price = price;
        this.quantity = quantity;
    }

    public WatchlistPositionFormDTO(@NonNull SecurityCompactDTO securityCompactDTO, int quantity)
    {
        this(securityCompactDTO.id, securityCompactDTO.lastPrice, quantity);
    }
    //</editor-fold>

    @JsonIgnore
    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }
}
