package com.androidth.general.api.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractStringDTOKey;

public class ExchangeStringId extends AbstractStringDTOKey
{
    public final static String BUNDLE_KEY_KEY = ExchangeStringId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ExchangeStringId(Bundle args)
    {
        super(args);
    }

    public ExchangeStringId(@NonNull String key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
