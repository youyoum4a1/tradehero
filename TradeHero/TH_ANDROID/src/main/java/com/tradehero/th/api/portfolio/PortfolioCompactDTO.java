package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:05 PM Copyright (c) TradeHero */
public class PortfolioCompactDTO implements DTO
{
    public static final String DEFAULT_TITLE = "Default";

    public int id;
    public Integer providerId;
    public String title;
    public double cashBalance;
    public double totalValue;
    public double totalExtraCashPurchased;
    public double totalExtraCashGiven;

    //<editor-fold desc="Constructors">
    public PortfolioCompactDTO()
    {
    }

    public PortfolioId getPortfolioId()
    {
        return new PortfolioId(id);
    }
    //</editor-fold>

    public boolean isDefault()
    {
        return DEFAULT_TITLE.equals(title);
    }

    public double getTotalExtraCash()
    {
        return totalExtraCashGiven + totalExtraCashPurchased;
    }

    @Override public int hashCode()
    {
        return new Integer(id).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PortfolioCompactDTO) && equals((PortfolioCompactDTO) other);
    }

    public boolean equals(PortfolioCompactDTO other)
    {
        if (other == null)
        {
            return false;
        }
        return new Integer(id).equals(other.id);
    }
}