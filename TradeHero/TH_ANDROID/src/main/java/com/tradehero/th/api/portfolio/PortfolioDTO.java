package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.quote.UpdatePricesQuoteDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:06 PM Copyright (c) TradeHero */
public class PortfolioDTO extends PortfolioCompactDTO implements DTO
{
    public double initialCash;
    public Date creationDate;
    public String description;
    public String Currency;

    public Double roiSinceInception;
    public Double roiSinceInceptionAnnualized;
    public double plSinceInception;

    public Double roiM2D;
    public Double roiM2DAnnualized;
    public double plM2D;

    public Double roiQ2D;
    public Double roiQ2DAnnualized;
    public double plQ2D;

    public Double roiY2D;
    public Double roiY2DAnnualized;
    public double plY2D;

    public List<UpdatePricesQuoteDTO> yahooSymbols;

    public int countTrades;
    public int countExchanges;

    @Override public String toString()
    {
        return "PortfolioDTO{" +
                "cashBalance=" + cashBalance +
                ", id=" + id +
                ", providerId=" + providerId +
                ", title='" + title + '\'' +
                ", totalValue=" + totalValue +
                ", totalExtraCashPurchased=" + totalExtraCashPurchased +
                ", totalExtraCashGiven=" + totalExtraCashGiven +
                ", isWatchlist=" + isWatchlist +
                ", openPositionsCount=" + openPositionsCount +
                ", closedPositionsCount=" + closedPositionsCount +
                ", watchlistPositionsCount=" + watchlistPositionsCount +
                ", markingAsOfUtc=" + markingAsOfUtc +
                ", countExchanges=" + countExchanges +

                ", initialCash=" + initialCash +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", Currency='" + Currency + '\'' +
                ", roiSinceInception=" + roiSinceInception +
                ", roiSinceInceptionAnnualized=" + roiSinceInceptionAnnualized +
                ", plSinceInception=" + plSinceInception +
                ", roiM2D=" + roiM2D +
                ", roiM2DAnnualized=" + roiM2DAnnualized +
                ", plM2D=" + plM2D +
                ", roiQ2D=" + roiQ2D +
                ", roiQ2DAnnualized=" + roiQ2DAnnualized +
                ", plQ2D=" + plQ2D +
                ", roiY2D=" + roiY2D +
                ", roiY2DAnnualized=" + roiY2DAnnualized +
                ", plY2D=" + plY2D +
                ", yahooSymbols=" + yahooSymbols +
                ", countTrades=" + countTrades +
                '}';
    }
}
