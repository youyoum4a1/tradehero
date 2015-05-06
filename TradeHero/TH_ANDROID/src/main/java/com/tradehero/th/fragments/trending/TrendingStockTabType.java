package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.StocksMainPositionListFragment;
import com.tradehero.th.fragments.watchlist.MainWatchlistPositionFragment;

public enum TrendingStockTabType
{
    StocksMain(R.string.stocks_main, StocksMainPositionListFragment.class),
    Trending(R.string.trending, TrendingStockFragment.class),
    Price(R.string.price, TrendingStockFragment.class),
    Volume(R.string.volume, TrendingStockFragment.class),
    All(R.string.all, TrendingStockFragment.class),
    Favorites(R.string.favorites, MainWatchlistPositionFragment.class),
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends DashboardFragment> fragmentClass;

    //<editor-fold desc="Constructors">
    private TrendingStockTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends DashboardFragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
    //</editor-fold>

    @NonNull public static TrendingStockTabType getDefault()
    {
        return TrendingStockTabType.StocksMain;
    }
}
