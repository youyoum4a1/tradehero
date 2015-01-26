package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public final class ExchangeListType extends AbstractStringDTOKey
{
    public final static String BUNDLE_KEY_KEY = ExchangeListType.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    //<editor-fold desc="Constructors">
    public ExchangeListType()
    {
        super(DEFAULT_KEY);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
