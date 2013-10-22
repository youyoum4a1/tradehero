package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 7:21 PM To change this template use File | Settings | File Templates. */
public class ExchangeListType extends AbstractStringDTOKey
{
    public static final String TAG = ExchangeListType.class.getSimpleName();
    public final static String BUNDLE_KEY_KEY = ExchangeListType.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    //<editor-fold desc="Constructors">
    public ExchangeListType()
    {
        this(DEFAULT_KEY);
    }

    public ExchangeListType(Bundle args)
    {
        super(args);
    }

    public ExchangeListType(String key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof ExchangeListType) && equals((ExchangeListType) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
