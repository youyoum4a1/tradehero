package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class ExchangeListType extends AbstractStringDTOKey
{
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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
