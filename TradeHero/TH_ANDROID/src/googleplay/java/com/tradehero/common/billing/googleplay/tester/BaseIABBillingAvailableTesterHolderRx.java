package com.tradehero.common.billing.googleplay.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.tester.BaseBillingAvailableTesterHolderRx;
import com.tradehero.common.billing.tester.BillingAvailableTesterRx;

abstract public class BaseIABBillingAvailableTesterHolderRx
        extends BaseBillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    public BaseIABBillingAvailableTesterHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected IABBillingAvailableTesterRx createTester(int requestCode);

    @Override public void onDestroy()
    {
        for (BillingAvailableTesterRx actor : actors.values())
        {
            ((IABBillingAvailableTesterRx) actor).onDestroy();
        }
        super.onDestroy();
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        IABBillingAvailableTesterRx actor = (IABBillingAvailableTesterRx) actors.get(requestCode);
        if (actor != null)
        {
            actor.onDestroy();
        }
        super.forgetRequestCode(requestCode);
    }
}
