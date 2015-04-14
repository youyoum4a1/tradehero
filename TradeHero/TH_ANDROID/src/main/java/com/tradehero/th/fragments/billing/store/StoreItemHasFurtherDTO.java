package com.tradehero.th.fragments.billing.store;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

public class StoreItemHasFurtherDTO extends StoreItemClickableDTO
{
    @Nullable public final Class<? extends Fragment> furtherFragment;
    @Nullable public final Class<? extends Activity> furtherActivity;

    //<editor-fold desc="Constructors">
    public StoreItemHasFurtherDTO(
            @StringRes int titleResId,
            @DrawableRes int iconResId,
            @Nullable Class<? extends Fragment> furtherFragment,
            @Nullable Class<? extends Activity> furtherActivity)
    {
        super(titleResId, iconResId);
        this.furtherFragment = furtherFragment;
        this.furtherActivity = furtherActivity;
        if (furtherFragment == null && furtherActivity == null)
        {
            throw new IllegalArgumentException("Both fragment and activity cannot be null");
        }
    }
    //</editor-fold>
}
