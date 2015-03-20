package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;

enum TrendingFXTabType
{
    FX(R.string.fx, TrendingFXFragment.class, AssetClass.FX)
    //STOCK(R.string.stocks, TrendingStockFragment.class, AssetClass.STOCKS)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends TrendingBaseFragment> fragmentClass;
    @NonNull public final AssetClass assetClass;

    //<editor-fold desc="Constructors">
    private TrendingFXTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends TrendingBaseFragment> fragmentClass,
            @NonNull AssetClass assetClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.assetClass = assetClass;
    }
    //</editor-fold>

    @NonNull static TrendingFXTabType getForAssetClass(@NonNull AssetClass assetClass)
    {
        for (TrendingFXTabType tabType : values())
        {
            if (tabType.assetClass.equals(assetClass))
            {
                return tabType;
            }
        }
        throw new IllegalArgumentException("Unknown AssetClass." + assetClass);
    }
}
