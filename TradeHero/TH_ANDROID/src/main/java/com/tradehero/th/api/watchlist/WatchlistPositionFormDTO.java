package com.ayondo.academy.api.watchlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ayondo.academy.api.security.SecurityIntegerId;

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
    //</editor-fold>

    @JsonIgnore
    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }
}
