package com.androidth.general.common.billing.tester;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeHolder;
import rx.Observable;

abstract public class BaseBillingAvailableTesterHolderRx
    extends BaseRequestCodeHolder<BillingAvailableTesterRx>
    implements BillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    public BaseBillingAvailableTesterHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> get(int requestCode)
    {
        BillingAvailableTesterRx tester = actors.get(requestCode);
        if (tester == null)
        {
            tester = createTester(requestCode);
            actors.put(requestCode, tester);
        }
        return tester.get();
    }

    @NonNull protected abstract BillingAvailableTesterRx createTester(int requestCode);
}
