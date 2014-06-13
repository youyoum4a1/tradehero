package com.tradehero.th.api.trade;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class TradeId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = TradeId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public TradeId(Bundle args)
    {
        super(args);
    }

    public TradeId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[TradeId key=%d]", key);
    }
}
