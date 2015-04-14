package com.tradehero.th.fragments.billing.store;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class StoreItemClickableDTO extends StoreItemDTO
{
    @DrawableRes public final int iconResId;

    //<editor-fold desc="Constructors">
    public StoreItemClickableDTO(@StringRes int titleResId, @DrawableRes int iconResId)
    {
        super(titleResId);
        this.iconResId = iconResId;
    }
    //</editor-fold>
}
