package com.ayondo.academy.fragments.trending;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.billing.BasePurchaseManagerFragment;
import com.ayondo.academy.fragments.position.FXMainPositionListFragment;

enum TrendingFXTabType
{
    Portfolio(R.string.my_fx, FXMainPositionListFragment.class),
    FX(R.string.trade_fx, TrendingFXFragment.class)
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

    @NonNull public static TrendingFXTabType getDefault()
    {
        return TrendingFXTabType.FX;
    }
}
