package com.tradehero.th.api.portfolio;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import timber.log.Timber;

public enum AssetClass
{
    STOCKS(1),
    FX(2),
    WARRANT(3),
    CFD(4);

    private int value;

    //<editor-fold desc="Constructors">
    AssetClass(int value)
    {
        this.value = value;
    }
    //</editor-fold>

    @JsonCreator @Nullable
    public static AssetClass create(int value)
    {
        for (AssetClass assetClass : values())
        {
            if (assetClass.value == value)
            {
                return assetClass;
            }
        }
        Timber.e(new IllegalArgumentException(), "Unknown PortfolioType value: %d", value);
        return null;
    }

    public int getValue()
    {
        return value;
    }
}
