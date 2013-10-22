package com.tradehero.th.api.trade;

import android.os.Bundle;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by julien on 22/10/13
 * Key identifying a Trade and all its parent objects.
 */
public class OwnedTradeId extends OwnedPositionId
{

    public final static String BUNDLE_KEY_TRADE_ID = OwnedTradeId.class.getName() + ".tradeId";
    public final Integer tradeId;

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

    @Override public int hashCode()
    {
        return super.hashCode() ^ tradeId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof OwnedTradeId))
        {
            return false;
        }
        return equals((OwnedTradeId) other);
    }

    public boolean equals(OwnedTradeId other)
    {
        if (other == null)
        {
            return false;
        }
        return super.equals((OwnedTradeId)other) && tradeId.equals(other.tradeId);
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == OwnedTradeId.class)
        {
            return compareTo((OwnedTradeId) o);
        }
        return o.getClass().getName().compareTo(OwnedTradeId.class.getName());
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

    @Override public boolean isValid()
    {
        return super.isValid() && this.tradeId != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_TRADE_ID, tradeId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; positionId=%d; tradeId=%d]", userId, portfolioId, positionId, tradeId);
    }
}
