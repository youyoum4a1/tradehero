package com.androidth.general.api.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;
import com.androidth.general.common.persistence.DTO;

public class ExchangeIntegerId extends AbstractIntegerDTOKey implements DTO
{
    public final static String BUNDLE_KEY_KEY = ExchangeIntegerId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ExchangeIntegerId(Bundle args)
    {
        super(args);
    }

    public ExchangeIntegerId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
