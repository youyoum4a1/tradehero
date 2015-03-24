package com.tradehero.th.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.position.StocksMainPositionListFragment;

enum TrendingFXTabType
{
    Portfolio(R.string.portfolio, StocksMainPositionListFragment.class),
    FX(R.string.fx, TrendingFXFragment.class)
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends BasePurchaseManagerFragment> fragmentClass;

    //<editor-fold desc="Constructors">
    private TrendingFXTabType(
            @StringRes int titleStringResId,
            @NonNull Class<? extends BasePurchaseManagerFragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
    //</editor-fold>

}
