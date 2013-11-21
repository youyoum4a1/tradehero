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
}
