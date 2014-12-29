package com.tradehero.th.api.portfolio;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import timber.log.Timber;

public enum PortfolioType
{
    STOCKS(1),
    FX(2),
    WARRANT(3),
    ;

    private int value;

    //<editor-fold desc="Constructors">
    PortfolioType(int value)
    {
        this.value = value;
    }
    //</editor-fold>

    @JsonCreator @Nullable
    public static PortfolioType create(int value)
    {
        for (PortfolioType portfolioType : values())
        {
            if (portfolioType.value == value)
            {
                return portfolioType;
            }
        }
        Timber.e(new IllegalArgumentException(), "Unknown PortfolioType value: %d", value);
        return null;
    }
}
