package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 2:38 PM To change this template use File | Settings | File Templates. */
public class PortfolioId extends AbstractIntegerDTOKey
{
    public final static String TAG = PortfolioId.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = PortfolioId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public PortfolioId(Bundle args)
    {
        super(args);
    }

    public PortfolioId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public boolean equals(Object obj)
    {
        if (!(obj instanceof PortfolioId))
        {
            return false;
        }
        return super.equals(obj);
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
