package com.tradehero.th.api.trade;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.users.UserBaseKey;

public class OwnedTradeId extends OwnedPositionId implements DTOKey
{
    public final static String BUNDLE_KEY_TRADE_ID = OwnedTradeId.class.getName() + ".tradeId";

    public final Integer tradeId;

    //<editor-fold desc="Constructors">
    public OwnedTradeId(Integer userId, Integer portfolioId, Integer positionId, Integer tradeId)
    {
        super(userId, portfolioId, positionId);
        this.tradeId = tradeId;
    }

    public OwnedTradeId(UserBaseKey userBaseKey, PortfolioId portfolioId, Integer positionId, Integer tradeId)
    {
        super(userBaseKey, portfolioId, positionId);
        this.tradeId = tradeId;
    }

    public OwnedTradeId(OwnedPositionId ownedPositionId, int tradeId)
    {
        super(ownedPositionId);
        this.tradeId = tradeId;
    }

    public OwnedTradeId(Bundle args)
    {
        super(args);
        this.tradeId = args.containsKey(BUNDLE_KEY_TRADE_ID) ? args.getInt(BUNDLE_KEY_TRADE_ID) : null;
    }

    public OwnedTradeId(OwnedTradeId ownedPositionId)
    {
        super(ownedPositionId);
        this.tradeId = ownedPositionId.tradeId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (tradeId == null ? 0 : tradeId.hashCode());
    }

    public boolean equals(OwnedTradeId other)
    {
        return (other != null) &&
                super.equals(other) &&
                (tradeId == null ? other.tradeId == null : tradeId.equals(other.tradeId));
    }

    public int compareTo(OwnedTradeId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return tradeId.compareTo(other.tradeId);
    }

    @JsonIgnore @Override public boolean isValid()
    {
        return super.isValid() && this.tradeId != null;
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_TRADE_ID, tradeId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; positionId=%d; tradeId=%d]", userId, portfolioId, positionId, tradeId);
    }

    @JsonIgnore public TradeId getTradeId()
    {
        return new TradeId(this.tradeId);
    }

}
