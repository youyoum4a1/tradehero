package com.tradehero.th.api.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import java.util.ArrayList;
import java.util.List;

public class PositionInPeriodDTO extends PositionDTO
{
    // This leaderboard Mark User Id needs to be populated by the service
    private Integer leaderboardMarkUserId;

    public Double totalPLInPeriodRefCcy;
    public Double marketValueStartPeriodRefCcy;
    public Double marketValueEndPeriodRefCcy;
    public Double sum_salesInPeriodRefCcy;
    public Double sum_purchasesInPeriodRefCcy;

    public boolean isProperInPeriod()
    {
        return marketValueStartPeriodRefCcy != null ||
                marketValueEndPeriodRefCcy != null ||
                sum_salesInPeriodRefCcy != null ||
                sum_purchasesInPeriodRefCcy != null;
    }

    public LeaderboardMarkUserPositionId getLbPositionId()
    {
        return new LeaderboardMarkUserPositionId(leaderboardMarkUserId);
    }

    @JsonIgnore
    @Override public PositionDTOKey getPositionDTOKey()
    {
        return new OwnedLeaderboardPositionId(leaderboardMarkUserId, id);
    }

    public LeaderboardMarkUserId getLeaderboardMarkUserId()
    {
        return new LeaderboardMarkUserId(leaderboardMarkUserId);
    }

    public void setLeaderboardMarkUserId(Integer leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    public OwnedLeaderboardPositionId getLbOwnedPositionId()
    {
        return new OwnedLeaderboardPositionId(userId, id);
    }

    public double getTotalInPeriodPL()
    {
        return totalPLInPeriodRefCcy == null ? 0 : totalPLInPeriodRefCcy;
    }

    public double getValueAtStart()
    {
        return marketValueStartPeriodRefCcy == null ? 0 : marketValueStartPeriodRefCcy;
    }

    public Double getROIInPeriod()
    {
        if (marketValueEndPeriodRefCcy == null || marketValueStartPeriodRefCcy == null)
        {
            return null;
        }

        double plInPeriod = getInPeriodPL();
        double investedInPeriod = getInvestedInPeriod();

        if (investedInPeriod == 0)
        {
            //throw new IllegalArgumentException("Wrong values " + this);
            return null;
        }

        return plInPeriod / investedInPeriod;
    }

    private double getInvestedInPeriod()
    {
        return marketValueStartPeriodRefCcy + sum_purchasesInPeriodRefCcy;
    }

    private double getInPeriodPL()
    {
        return marketValueEndPeriodRefCcy + sum_salesInPeriodRefCcy - getInvestedInPeriod();
    }

    @Override public String toString()
    {
        return "PositionInPeriodDTO{" +
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
                ", earliestTradeUtc=" + earliestTradeUtc +
                ", latestTradeUtc=" + latestTradeUtc +
                ", sumInvestedAmountRefCcy=" + sumInvestedAmountRefCcy +
                ", totalTransactionCostRefCcy=" + totalTransactionCostRefCcy +
                ", aggregateCount=" + aggregateCount +
                ", leaderboardMarkUserId=" + leaderboardMarkUserId +
                ", totalPLInPeriodRefCcy=" + totalPLInPeriodRefCcy +
                ", marketValueStartPeriodRefCcy=" + marketValueStartPeriodRefCcy +
                ", marketValueEndPeriodRefCcy=" + marketValueEndPeriodRefCcy +
                ", sum_salesInPeriodRefCcy=" + sum_salesInPeriodRefCcy +
                ", sum_purchasesInPeriodRefCcy=" + sum_purchasesInPeriodRefCcy +
                ", extras={" + formatExtras(", ").toString() + "}" +
                '}';
    }
}
