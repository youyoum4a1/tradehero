package com.androidth.general.api.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOKey;
import com.androidth.general.api.position.OwnedPositionId;

public class OwnedTradeId extends OwnedPositionId implements DTOKey
{
    public final static String BUNDLE_KEY_TRADE_ID = OwnedTradeId.class.getName() + ".tradeId";

    @NonNull public final Integer tradeId;

    //<editor-fold desc="Constructors">
    public OwnedTradeId(int userId, int portfolioId, int positionId, int tradeId)
    {
        super(userId, portfolioId, positionId);
        this.tradeId = tradeId;
    }

    public OwnedTradeId(@NonNull Bundle args)
    {
        super(args);
        this.tradeId = args.getInt(BUNDLE_KEY_TRADE_ID);
    }
    //</editor-fold>

    public static boolean isOwnedTradeId(@NonNull Bundle args)
    {
        return isOwnedPositionId(args)
                && args.containsKey(BUNDLE_KEY_TRADE_ID);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ tradeId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull OwnedPositionId other)
    {
        return (other instanceof OwnedTradeId)
                && super.equalFields(other);
    }

    protected boolean equalFields(@NonNull OwnedTradeId other)
    {
        return super.equalFields(other) &&
                tradeId.equals(other.tradeId);
    }

    public int compareTo(@NonNull OwnedTradeId other)
    {
        if (this == other)
        {
            return 0;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return tradeId.compareTo(other.tradeId);
    }

    @Override protected void putParameters(@NonNull Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_TRADE_ID, tradeId);
    }

    @Override @NonNull public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; positionId=%d; tradeId=%d]", userId, portfolioId, positionId, tradeId);
    }
}
