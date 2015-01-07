package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;

enum TrendingTabType
{
    STOCK(R.string.stocks, TrendingStockFragment.class, AssetClass.STOCKS),
    FX(R.string.fx, TrendingFXFragment.class, AssetClass.FX)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;
    @NonNull public final AssetClass assetClass;

    TrendingTabType(@StringRes int titleStringResId,
            @NonNull Class<? extends Fragment> fragmentClass,
            @NonNull AssetClass assetClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.assetClass = assetClass;
    }

    static TrendingTabType getForAssetClass(@NonNull AssetClass assetClass)
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
