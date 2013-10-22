package com.tradehero.th.api.trade;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by julien on 22/10/13
 */
public class TradeId extends AbstractIntegerDTOKey
{
    public final static String TAG = TradeId.class.getSimpleName();
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

    @Override public boolean equals(Object other)
    {
        if (!(other instanceof TradeId))
        {
            return false;
        }
        return super.equals(other);
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
