package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 6:33 PM To change this template use File | Settings | File Templates. */
public class ExchangeStringId extends AbstractStringDTOKey
{
    public static final String TAG = ExchangeStringId.class.getSimpleName();
    public final static String BUNDLE_KEY_KEY = ExchangeStringId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ExchangeStringId(Bundle args)
    {
        super(args);
    }

    public ExchangeStringId(String key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof ExchangeStringId) && equals((ExchangeStringId) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
