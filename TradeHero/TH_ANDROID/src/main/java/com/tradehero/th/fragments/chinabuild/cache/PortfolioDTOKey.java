package com.tradehero.th.fragments.chinabuild.cache;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class PortfolioDTOKey extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = PortfolioDTOKey.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    public int competitionId;

    public PortfolioDTOKey(Bundle args)
    {
        super(args);
    }

    public PortfolioDTOKey(Integer key)
    {
        super(key);
        competitionId = key;
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

}
