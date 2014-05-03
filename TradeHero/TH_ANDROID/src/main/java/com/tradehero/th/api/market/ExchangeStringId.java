package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;


public class ExchangeStringId extends AbstractStringDTOKey
{
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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
