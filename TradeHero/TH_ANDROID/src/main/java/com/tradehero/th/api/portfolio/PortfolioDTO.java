package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.quote.UpdatePricesQuoteDTO;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PortfolioDTO extends PortfolioCompactDTO implements DTO
{
    public double initialCash;
    public Date creationDate;
    public String description;

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

    @Override @NotNull public String toString()
    {
        return "[PortfolioDTO " +
                super.toString() +
                ", initialCash=" + initialCash +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", roiSinceInception=" + roiSinceInception +
                ", roiSinceInceptionAnnualized=" + roiSinceInceptionAnnualized +
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
                ']';
    }
}
