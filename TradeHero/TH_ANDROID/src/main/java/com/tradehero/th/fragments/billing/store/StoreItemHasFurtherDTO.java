package com.tradehero.th.fragments.billing.store;

import android.support.v4.app.Fragment;

public class StoreItemHasFurtherDTO extends StoreItemClickableDTO
{
    public Class<? extends Fragment> furtherFragment;

    //<editor-fold desc="Constructors">
    public StoreItemHasFurtherDTO(
            int titleResId,
            int iconResId,
            Class<? extends Fragment> furtherFragment)
    {
        super(titleResId, iconResId);
        this.furtherFragment = furtherFragment;
    }
    //</editor-fold>
}
