package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;

enum TrendingStockTabType
{
    Trending(R.string.trending, TrendingStockFragment.class),
    Price(R.string.price, TrendingStockFragment.class),
    Volume(R.string.volume, TrendingStockFragment.class),
    All(R.string.all, TrendingStockFragment.class)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends TrendingBaseFragment> fragmentClass;

    //<editor-fold desc="Constructors">
    private TrendingStockTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends TrendingBaseFragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        //this.assetClass = assetClass;
    }
    //</editor-fold>

}
