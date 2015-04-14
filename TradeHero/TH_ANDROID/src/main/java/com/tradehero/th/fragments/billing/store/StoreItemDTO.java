package com.tradehero.th.fragments.billing.store;

import android.support.annotation.StringRes;

public class StoreItemDTO
{
    @StringRes public final int titleResId;

    //<editor-fold desc="Constructors">
    public StoreItemDTO(@StringRes int titleResId)
    {
        this.titleResId = titleResId;
    }
    //</editor-fold>
}
