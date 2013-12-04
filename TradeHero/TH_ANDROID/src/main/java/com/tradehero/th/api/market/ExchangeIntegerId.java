package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.common.persistence.DTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 6:30 PM To change this template use File | Settings | File Templates. */
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
