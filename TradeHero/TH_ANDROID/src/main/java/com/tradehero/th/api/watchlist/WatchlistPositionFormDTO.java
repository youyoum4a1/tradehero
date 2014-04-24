package com.tradehero.th.api.watchlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.security.SecurityIntegerId;

public class WatchlistPositionFormDTO
{
    public int securityId;
    public double price;
    public int quantity;

    public WatchlistPositionFormDTO(int securityId, double price, int quantity)
    {
        this.securityId = securityId;
        this.price = price;
        this.quantity = quantity;
    }

    @JsonIgnore
    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }
}
