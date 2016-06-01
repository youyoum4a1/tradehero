package com.ayondo.academy.api.portfolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;

public class PortfolioId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = PortfolioId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public PortfolioId()
    {
    }

    public PortfolioId(@NonNull Bundle args)
    {
        super(args);
    }

    public PortfolioId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @RouteProperty
    public void setPortfolioId(int portfolioId)
    {
        this.key = portfolioId;
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[PortfolioId key=%d]", key);
    }
}
