package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class PortfolioId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = PortfolioId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public PortfolioId(Bundle args)
    {
        super(args);
    }

    public PortfolioId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[PortfolioId key=%d]", key);
    }
}
