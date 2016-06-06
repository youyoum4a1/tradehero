package com.androidth.general.common.billing.samsung.tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.tester.BaseBillingAvailableTesterHolderRx;

abstract public class BaseSamsungBillingAvailableTesterHolderRx
        extends BaseBillingAvailableTesterHolderRx
        implements SamsungBillingAvailableTesterHolderRx
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungBillingAvailableTesterHolderRx(@NonNull Context context, @SamsungBillingMode int mode)
    {
        this.context = context;
        this.mode = mode;
    }
    //</editor-fold>

    @NonNull @Override abstract protected SamsungBillingAvailableTesterRx createTester(int requestCode);

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
