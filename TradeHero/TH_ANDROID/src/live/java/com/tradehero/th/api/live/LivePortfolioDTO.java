package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import java.util.Calendar;
import java.util.Date;

public class LivePortfolioDTO extends PortfolioDTO implements DTO
{
    public double margin;

    @Override @NonNull public String toString()
    {
        return "[LivePortfolioDTO " +
                super.toString() +
                ", margin =" + margin +
                "]";
    }

    // Dummy Live Portfolio
    public static LivePortfolioDTO createForDummy()
    {
        LivePortfolioDTO livePortfolioDTO = new LivePortfolioDTO();
        livePortfolioDTO.cashBalanceRefCcy = 9999.7886;
        livePortfolioDTO.id = 88888;
        livePortfolioDTO.providerId = null;
        livePortfolioDTO.assetClass = AssetClass.CFD;
        livePortfolioDTO.title = "Live Portfolios";
        livePortfolioDTO.totalValue = 19999.7886;
        livePortfolioDTO.totalExtraCashPurchased = 0.0;
        livePortfolioDTO.totalExtraCashGiven = 0.0;
        livePortfolioDTO.roiSinceInception = -0.0457;
        livePortfolioDTO.isWatchlist = false;
        livePortfolioDTO.openPositionsCount = 10;
        livePortfolioDTO.closedPositionsCount = 10;
        livePortfolioDTO.watchlistPositionsCount = 0;
        livePortfolioDTO.markingAsOfUtc = new Date();
        livePortfolioDTO.currencyDisplay = "US$";
        livePortfolioDTO.refCcyToUsdRate = 1.0;
        livePortfolioDTO.txnCostUsd = null;
        livePortfolioDTO.userId = 6627;
        livePortfolioDTO.marginAvailableRefCcy = null;
        livePortfolioDTO.marginCloseOutPercent = null;
        livePortfolioDTO.leverage = null;
        livePortfolioDTO.initialCash = 100000.0;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 9, 21, 8, 21, 4);

        livePortfolioDTO.creationDate = calendar.getTime();
        livePortfolioDTO.description = "Stocks - Live";
        livePortfolioDTO.roiSinceInception = -0.0457;
        livePortfolioDTO.roiSinceInceptionAnnualized = -0.0163;
        livePortfolioDTO.roiM2D = 0.0;
        livePortfolioDTO.roiM2DAnnualized = 0.0;
        livePortfolioDTO.plM2D = 0.0;
        livePortfolioDTO.roiQ2D = 0.0;
        livePortfolioDTO.roiQ2DAnnualized = 0.0;
        livePortfolioDTO.plQ2D = 0.0;
        livePortfolioDTO.roiY2D = 0.0;
        livePortfolioDTO.roiY2DAnnualized = 0.0;
        livePortfolioDTO.plY2D = 0.0;
        livePortfolioDTO.countTrades = 24;

        return livePortfolioDTO;
    }
}
