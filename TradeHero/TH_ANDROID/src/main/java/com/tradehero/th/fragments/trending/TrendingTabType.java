package com.ayondo.academy.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.ayondo.academy.R;
import com.ayondo.academy.api.portfolio.AssetClass;

enum TrendingTabType
{
    STOCK(R.string.stocks, TrendingStockFragment.class, AssetClass.STOCKS),
    FX(R.string.fx, TrendingFXFragment.class, AssetClass.FX)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends TrendingBaseFragment> fragmentClass;
    @NonNull public final AssetClass assetClass;

    //<editor-fold desc="Constructors">
    private TrendingTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends TrendingBaseFragment> fragmentClass,
            @NonNull AssetClass assetClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.assetClass = assetClass;
    }
    //</editor-fold>

    @NonNull static TrendingTabType getForAssetClass(@NonNull AssetClass assetClass)
    {
        for (TrendingTabType tabType : values())
        {
            if (tabType.assetClass.equals(assetClass))
            {
                return tabType;
            }
        }
        throw new IllegalArgumentException("Unknown AssetClass." + assetClass);
    }
}
