package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;

public enum TrendingStockTabType
{
    Trending(R.string.trending_tab_trending),
    Price(R.string.trending_tab_price),
    Volume(R.string.trending_tab_volume),
    All(R.string.trending_tab_all),;

    @StringRes public final int titleStringResId;

    //<editor-fold desc="Constructors">
    TrendingStockTabType(
            @StringRes int titleStringResId)
    {
        this.titleStringResId = titleStringResId;
    }
    //</editor-fold>

    @NonNull public static TrendingStockTabType getDefault()
    {
        return TrendingStockTabType.Trending;
    }
}
