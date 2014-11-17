package com.tradehero.common.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.SamsungIapBindOperator;
import com.tradehero.common.billing.tester.BillingTestResult;
import rx.Observable;

abstract public class BaseSamsungBillingAvailableTesterRx
        extends BaseSamsungActorRx<BillingTestResult>
        implements SamsungBillingAvailableTesterRx
{
    //<editor-fold desc="Description">
    public BaseSamsungBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            int mode)
    {
        super(requestCode, context, mode);
        test();
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> get()
    {
        return replayObservable;
    }

    protected void test()
    {
        Observable.create(new SamsungIapBindOperator(context, mode))
                .map(result -> new BillingTestResult(getRequestCode()))
                .subscribe(subject);
    }
}
