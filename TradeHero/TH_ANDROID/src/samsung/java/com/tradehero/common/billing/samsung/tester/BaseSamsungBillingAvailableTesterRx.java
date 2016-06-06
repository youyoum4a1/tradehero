package com.androidth.general.common.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.BaseSamsungActorRx;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.rx.SamsungIapHelperFacade;
import com.androidth.general.common.billing.tester.BillingTestResult;
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
            @SamsungBillingMode int mode)
    {
        super(requestCode, context, mode);
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> get()
    {
        return SamsungIapHelperFacade.bind(context, mode)
                .map(new Func1<Integer, BillingTestResult>()
                {
                    @Override public BillingTestResult call(Integer result)
                    {
                        return new BillingTestResult(BaseSamsungBillingAvailableTesterRx.this.getRequestCode());
                    }
                });
    }
}
