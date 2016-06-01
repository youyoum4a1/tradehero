package com.ayondo.academy.fragments.billing.store;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.tradehero.common.persistence.DTO;

public abstract class StoreItemDisplayDTO implements DTO
{
    @DrawableRes public final int iconResId;
    @StringRes public final int titleResId;
    public final int displayOrder;

    public StoreItemDisplayDTO(int iconResId, int titleResId, int displayOrder)
    {
        this.iconResId = iconResId;
        this.titleResId = titleResId;
        this.displayOrder = displayOrder;
    }
}
