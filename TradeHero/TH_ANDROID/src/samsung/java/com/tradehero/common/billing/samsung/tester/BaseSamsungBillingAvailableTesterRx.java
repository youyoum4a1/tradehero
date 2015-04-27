package com.tradehero.common.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.rx.SamsungIapBindOperator;
import com.tradehero.common.billing.tester.BillingTestResult;
import rx.Observable;
import rx.functions.Func1;

abstract public class BaseSamsungBillingAvailableTesterRx
        extends BaseSamsungActorRx
        implements SamsungBillingAvailableTesterRx
{
    //<editor-fold desc="Description">
    public BaseSamsungBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            int mode)
    {
        super(requestCode, context, mode);
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> get()
    {
        return Observable.create(new SamsungIapBindOperator(context, mode))
                .map(new Func1<Integer, BillingTestResult>()
                {
                    @Override public BillingTestResult call(Integer result)
                    {
                        return new BillingTestResult(BaseSamsungBillingAvailableTesterRx.this.getRequestCode());
                    }
                });
    }
}
