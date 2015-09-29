package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public enum TrendingStockTabType
{
    Trending(R.string.trending_tab_trending, TrendingStockFragment.class, true),
    Price(R.string.trending_tab_price, TrendingStockFragment.class, true),
    Volume(R.string.trending_tab_volume, TrendingStockFragment.class, true),
    All(R.string.trending_tab_all, TrendingStockFragment.class, true),
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends DashboardFragment> fragmentClass;
    public final boolean showExchangeSelection;

    //<editor-fold desc="Constructors">
    TrendingStockTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends DashboardFragment> fragmentClass,
            boolean showExchangeSelection)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.showExchangeSelection = showExchangeSelection;
    }
    //</editor-fold>

    @NonNull public static TrendingStockTabType getDefault()
    {
        return TrendingStockTabType.Trending;
    }
}
