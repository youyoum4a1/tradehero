package com.tradehero.common.billing.samsung.tester;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.tester.BaseBillingAvailableTesterHolderRx;

abstract public class BaseSamsungBillingAvailableTesterHolderRx
        extends BaseBillingAvailableTesterHolderRx
        implements SamsungBillingAvailableTesterHolderRx
{
    @NonNull protected final Context context;
    protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungBillingAvailableTesterHolderRx(@NonNull Context context, int mode)
    {
        this.context = context;
        this.mode = mode;
    }
    //</editor-fold>

    @NonNull @Override abstract protected SamsungBillingAvailableTesterRx createTester(int requestCode);

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
