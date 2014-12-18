package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;

enum TrendingTabType
{
    STOCK(R.string.stocks, TrendingStockFragment.class),
    FX(R.string.fx, TrendingFXFragment.class)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;

    TrendingTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
