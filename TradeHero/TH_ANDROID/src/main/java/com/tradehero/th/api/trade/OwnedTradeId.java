package com.tradehero.th.api.trade;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.position.OwnedPositionId;
import org.jetbrains.annotations.NotNull;

public class OwnedTradeId extends OwnedPositionId implements DTOKey
{
    public final static String BUNDLE_KEY_TRADE_ID = OwnedTradeId.class.getName() + ".tradeId";

    @NotNull public final Integer tradeId;

    //<editor-fold desc="Constructors">
    public OwnedTradeId(int userId, int portfolioId, int positionId, int tradeId)
    {
        super(userId, portfolioId, positionId);
        this.tradeId = tradeId;
    }

    public OwnedTradeId(Bundle args)
    {
        super(args);
        this.tradeId = args.getInt(BUNDLE_KEY_TRADE_ID);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ tradeId.hashCode();
    }

    public boolean equals(OwnedTradeId other)
    {
        return (other != null) &&
                super.equals(other) &&
                tradeId.equals(other.tradeId);
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

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_TRADE_ID, tradeId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; positionId=%d; tradeId=%d]", userId, portfolioId, positionId, tradeId);
    }
}
