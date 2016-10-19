package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.api.users.UserBaseKey;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionDTO extends PositionDTOCompact
{
    public int userId;
    public int securityId;
    // This value is always in the portfolio currency
    public Double realizedPLRefCcy;
    // This value is always in the portfolio currency
    public Double unrealizedPLRefCcy;
    // This value is always in the portfolio currency
    public double marketValueRefCcy;
    public Date earliestTradeUtc;
    public Date latestTradeUtc;//precise date

    // This value is always in the portfolio currency
    public Double sumInvestedAmountRefCcy;

    // This value is always in the portfolio currency
    public double totalTransactionCostRefCcy;

    // if >1, then the values above relate to a collection of positions, not a single position -- see: MaskOpenPositions()
    public int aggregateCount;

    public Date markDateUtc;
    public Integer latestPortfolioMarkId;
    public Double markPrice;

    //<editor-fold desc="Constructors">
    public PositionDTO()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore
    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore
    public OwnedPortfolioId getOwnedPortfolioId()
    {
        return new OwnedPortfolioId(userId, portfolioId);
    }

    @JsonIgnore
    public OwnedPositionId getOwnedPositionId()
    {
        return new OwnedPositionId(userId, portfolioId, id);
    }

    @JsonIgnore
    public PositionDTOKey getPositionDTOKey()
    {
        return getOwnedPositionId();
    }

    @NonNull public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }

    @Nullable public Double getROISinceInception()
    {
        if (shares == null || realizedPLRefCcy == null || unrealizedPLRefCcy == null)
        {
            return null;
        }

        double numberToDisplay = realizedPLRefCcy;
        Boolean isOpen = isOpen();
        if (isOpen != null && isOpen)
        {
            numberToDisplay += unrealizedPLRefCcy;
        }

        // divide by cost basis, if possible
        if (sumInvestedAmountRefCcy == null || sumInvestedAmountRefCcy == 0)
        {
            return null;
        }
        else
        {
            numberToDisplay /= sumInvestedAmountRefCcy ;
        }
        return numberToDisplay;
    }

    public boolean isLocked()
    {
        return this.securityId < 0;
    }

    @Override public String toString()
    {
        return "PositionDTO{" +
                "id=" + id +
                ", shares=" + shares +
                ", portfolioId=" + portfolioId +
                ", averagePriceRefCcy=" + averagePriceRefCcy +
                ", currencyDisplay=" + currencyDisplay +
                ", currencyISO=" + currencyISO +
                ", userId=" + userId +
                ", securityId=" + securityId +
                ", realizedPLRefCcy=" + realizedPLRefCcy +
                ", unrealizedPLRefCcy=" + unrealizedPLRefCcy +
                ", marketValueRefCcy=" + marketValueRefCcy +
//                ", earliestTradeUtc=" + earliestTradeUtc +
//                ", latestTradeUtc=" + latestTradeUtc +
                ", sumInvestedAmountRefCcy=" + sumInvestedAmountRefCcy +
                ", totalTransactionCostRefCcy=" + totalTransactionCostRefCcy +
                ", aggregateCount=" + aggregateCount +
                ", markDateUtc=" + markDateUtc +
                ", latestPortfolioMarkId=" + latestPortfolioMarkId +
                ", markPrice=" + markPrice +
                '}';
    }

    public Date getLatestTradeUtc() {
        //2016-08-31T03:24:38
//        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_STANDARD);
//        try{
//            Date date = format.parse(latestTradeUtc);
//            return date;
//        }catch (ParseException e){
//            e.printStackTrace();
//            return null;
//        }
        return latestTradeUtc;
    }

    public void setLatestTradeUtc(Date newDate) {
        //2016-08-31T03:24:38
//        return null;
    }

    public Date getEarliestTradeUtc() {
        //2016-08-28T04:40:31.003
//        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_STANDARD);
//        try{
//            Date date = format.parse(earliestTradeUtc);
//            return date;
//        }catch (ParseException e){
//            return null;
//        }
        return earliestTradeUtc;
    }


}
