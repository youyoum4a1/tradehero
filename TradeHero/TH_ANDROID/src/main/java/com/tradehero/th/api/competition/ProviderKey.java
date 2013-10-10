package com.tradehero.th.api.competition;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 5:13 PM To change this template use File | Settings | File Templates. */
public class ProviderKey extends AbstractIntegerDTOKey
{
    public final static String TAG = ProviderKey.class.getSimpleName();
    public final static String BUNDLE_KEY_KEY = ProviderKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ProviderKey(Integer key)
    {
        super(key);
    }

    public ProviderKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
