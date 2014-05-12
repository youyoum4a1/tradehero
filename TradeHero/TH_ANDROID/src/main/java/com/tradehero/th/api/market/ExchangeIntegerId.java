package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.common.persistence.DTO;


public class ExchangeIntegerId extends AbstractIntegerDTOKey implements DTO
{
    public static final String TAG = ExchangeIntegerId.class.getSimpleName();
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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
