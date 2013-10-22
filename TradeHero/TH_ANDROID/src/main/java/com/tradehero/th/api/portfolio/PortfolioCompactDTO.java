package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:05 PM Copyright (c) TradeHero */
public class PortfolioCompactDTO implements DTO
{
    public int id;
    public Integer providerId;
    public String title;
    public double cashBalance;
    public double totalValue;
    public double totalExtraCashPurchased;
    public double totalExtraCashGiven;

    public PortfolioCompactDTO()
    {
    }

    public PortfolioId getPortfolioId()
    {
        return new PortfolioId(id);
    }
}